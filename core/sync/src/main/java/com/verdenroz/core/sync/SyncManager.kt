package com.verdenroz.core.sync

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.verdenroz.core.datastore.UserSettingsStore
import com.verdenroz.verdaxmarket.core.common.dispatchers.Dispatcher
import com.verdenroz.verdaxmarket.core.common.dispatchers.FinanceQueryDispatchers
import com.verdenroz.verdaxmarket.core.common.dispatchers.di.ApplicationScope
import com.verdenroz.verdaxmarket.core.data.model.asExternalModel
import com.verdenroz.verdaxmarket.core.data.repository.WatchlistRepository
import com.verdenroz.verdaxmarket.core.model.RegionFilter
import com.verdenroz.verdaxmarket.core.model.ThemePreference
import com.verdenroz.verdaxmarket.core.model.UserSetting
import com.verdenroz.verdaxmarket.core.model.WatchlistQuote
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class SyncManager @Inject constructor(
    private val database: FirebaseDatabase,
    private val auth: FirebaseAuth,
    private val userSettingsStore: UserSettingsStore,
    private val watchlistRepository: WatchlistRepository,
    @Dispatcher(FinanceQueryDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    @ApplicationScope private val scope: CoroutineScope,
) {

    data class SyncState(
        val isEnabled: Boolean = false,
        val isLoggedIn: Boolean = false,
        val isSyncing: Boolean = false,
        val isInitialized: Boolean = false,
        val error: Exception? = null
    )

    private val currentUserId: String?
        get() = auth.currentUser?.uid

    private var isListening = false
    private var settingsJob: Job? = null
    private var watchlistJob: Job? = null

    private val _syncState = MutableStateFlow(SyncState())
    val syncState = _syncState.asStateFlow()

    private val FirebaseAuth.authStateFlow: Flow<FirebaseUser?>
        get() = callbackFlow {
            val authStateListener = FirebaseAuth.AuthStateListener { auth ->
                trySend(auth.currentUser)
            }
            addAuthStateListener(authStateListener)
            awaitClose { removeAuthStateListener(authStateListener) }
        }


    val isSyncEnabled = userSettingsStore.userSettings
        .map { it.isSynced }
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    init {
        scope.launch {
            // Monitor both sync settings and auth state
            combine(
                isSyncEnabled,
                auth.authStateFlow,
                _syncState
            ) { isSyncEnabled, user, currentState ->
                currentState.copy(
                    isEnabled = isSyncEnabled,
                    isLoggedIn = user != null
                )
            }.collect { state ->
                _syncState.value = state
                when {
                    // Start sync if enabled, logged in and not already syncing
                    state.isEnabled && state.isLoggedIn && !state.isSyncing -> {
                        initializeSync()
                    }
                    // Stop sync if disabled, logged out or already syncing
                    !state.isEnabled || !state.isLoggedIn -> {
                        stopSync()
                    }
                }
            }
        }

        // Sets sync to false if an error occurs
        scope.launch {
            _syncState.collect { state ->
                if (state.error != null) {
                    userSettingsStore.setSync(false)
                }
            }
        }
    }

    fun retrySync() {
        scope.launch {
            _syncState.update { it.copy(error = null) }
            // This will trigger the combine flow in init which will call initializeSync()
            userSettingsStore.setSync(true)
        }
    }

    /**
     * Starts the sync process when the user is logged in and sync is enabled.
     */
    private suspend fun initializeSync() {
        if (auth.currentUser == null || !_syncState.value.isEnabled) return

        _syncState.update { it.copy(isSyncing = true, error = null) }

        withContext(ioDispatcher) {
            try {
                // Initial sync of settings
                syncSettings()

                // Initial sync of watchlist
                syncWatchlist()

                // Setup change listeners
                setupChangeListeners()

                _syncState.update { it.copy(isInitialized = true) }
            } catch (e: Exception) {
                _syncState.update { it.copy(error = e) }
            }
        }
    }

    /**
     * Syncs the user settings with the cloud.
     */
    private suspend fun syncSettings() {
        val cloudSettings = getCloudSettings()
        val localSettings = userSettingsStore.userSettings.first()

        if (cloudSettings != null) {
            userSettingsStore.updateSettings(cloudSettings)
        } else {
            syncCloudSettings(localSettings)
        }
    }

    /**
     * Syncs the user settings with the cloud.
     */
    private suspend fun syncWatchlist() {
        val localWatchlist = watchlistRepository.getWatchlist().map { it.asExternalModel() }
        val cloudWatchlist = getCloudWatchlist()

        when {
            cloudWatchlist != null && localWatchlist.isEmpty() -> {
                watchlistRepository.updateWatchlist(cloudWatchlist)
            }

            cloudWatchlist != null -> {
                val mergedWatchlist = localWatchlist.mergeWith(cloudWatchlist)
                watchlistRepository.updateWatchlist(mergedWatchlist)
            }

            localWatchlist.isNotEmpty() -> {
                syncCloudWatchlist(localWatchlist)
            }
        }
    }

    /**
     * Sets up change listeners for user settings and watchlist.
     */
    @OptIn(FlowPreview::class)
    private fun setupChangeListeners() {
        if (isListening) return
        isListening = true

        // Listen for changes in user settings
        settingsJob?.cancel()
        settingsJob = scope.launch(ioDispatcher) {
            userSettingsStore.userSettings
                .debounce(500)
                .collect { settings ->
                    if (auth.currentUser != null && _syncState.value.isEnabled) {
                        try {
                            syncCloudSettings(settings)
                        } catch (e: Exception) {
                            _syncState.update { it.copy(error = e) }
                        }
                    }
                }
        }

        // Listen for changes in watchlist
        watchlistJob?.cancel()
        watchlistJob = scope.launch(ioDispatcher) {
            watchlistRepository.watchlist
                .debounce(500)
                .collect { watchlist ->
                    if (auth.currentUser != null && _syncState.value.isEnabled) {
                        try {
                            syncCloudWatchlist(watchlist)
                        } catch (e: Exception) {
                            _syncState.update { it.copy(error = e) }
                        }
                    }
                }
        }
    }

    /**
     * Stops syncing user settings and watchlist.
     */
    private fun stopSync() {
        isListening = false
        settingsJob?.cancel()
        watchlistJob?.cancel()
        _syncState.update { it.copy(isSyncing = false, isInitialized = false) }
    }

    /**
     * Syncs the user settings with the Realtime Database.
     */
    private suspend fun syncCloudSettings(settings: UserSetting) {
        currentUserId?.let { userId ->
            val settingsData = mapOf(
                "themePreference" to settings.themePreference.name,
                "regionPreference" to settings.regionPreference.name,
                "hintsEnabled" to settings.hintsEnabled,
                "showMarketHours" to settings.showMarketHours,
            )

            suspendCancellableCoroutine { continuation ->
                database.reference
                    .child("users")
                    .child(userId)
                    .child("settings")
                    .setValue(settingsData)
                    .addOnSuccessListener { continuation.resume(Unit) }
                    .addOnFailureListener { e ->
                        _syncState.update { it.copy(error = e) }
                        continuation.resumeWithException(e)
                    }
            }
        }
    }

    /**
     * Gets the user settings from the Realtime Database.
     */
    private suspend fun getCloudSettings(): UserSetting? {
        return currentUserId?.let { userId ->
            suspendCancellableCoroutine { continuation ->
                database.reference
                    .child("users")
                    .child(userId)
                    .child("settings")
                    .get()
                    .addOnSuccessListener { snapshot ->
                        if (!snapshot.exists()) {
                            continuation.resume(null)
                            return@addOnSuccessListener
                        }

                        val settings = UserSetting(
                            themePreference = ThemePreference.valueOf(
                                snapshot.child("themePreference").value as? String
                                    ?: ThemePreference.SYSTEM.name
                            ),
                            regionPreference = RegionFilter.valueOf(
                                snapshot.child("regionPreference").value as? String
                                    ?: RegionFilter.US.name
                            ),
                            hintsEnabled = snapshot.child("hintsEnabled").value as? Boolean ?: true,
                            showMarketHours = snapshot.child("showMarketHours").value as? Boolean
                                ?: true,
                        )
                        continuation.resume(settings)
                    }
                    .addOnFailureListener { e ->
                        _syncState.update { it.copy(error = e) }
                        continuation.resumeWithException(e)
                    }
            }
        }
    }

    /**
     * Syncs the watchlist with the Realtime Database.
     */
    private suspend fun syncCloudWatchlist(watchlist: List<WatchlistQuote>) {
        currentUserId?.let { userId ->
            val watchlistData = watchlist.map { quote ->
                mapOf(
                    "symbol" to quote.symbol,
                    "name" to quote.name,
                    "logo" to quote.logo,
                    "order" to quote.order
                )
            }

            suspendCancellableCoroutine { continuation ->
                database.reference
                    .child("users")
                    .child(userId)
                    .child("watchlist")
                    .setValue(watchlistData)
                    .addOnSuccessListener { continuation.resume(Unit) }
                    .addOnFailureListener { e ->
                        _syncState.update { it.copy(error = e) }
                        continuation.resumeWithException(e)
                    }
            }
        }
    }

    /**
     * Gets the watchlist from the Realtime Database.
     */
    private suspend fun getCloudWatchlist(): List<WatchlistQuote>? {
        return currentUserId?.let { userId ->
            suspendCancellableCoroutine { continuation ->
                database.reference
                    .child("users")
                    .child(userId)
                    .child("watchlist")
                    .get()
                    .addOnSuccessListener { snapshot ->
                        if (!snapshot.exists()) {
                            continuation.resume(null)
                            return@addOnSuccessListener
                        }

                        val watchlist = snapshot.children.mapNotNull { itemSnapshot ->
                            WatchlistQuote(
                                symbol = itemSnapshot.child("symbol").value as? String
                                    ?: return@mapNotNull null,
                                name = itemSnapshot.child("name").value as? String
                                    ?: return@mapNotNull null,
                                logo = itemSnapshot.child("logo").value as? String,
                                order = (itemSnapshot.child("order").value as? Long)?.toInt()
                                    ?: return@mapNotNull null,
                                price = null,
                                change = null,
                                percentChange = null
                            )
                        }
                        continuation.resume(watchlist)
                    }
                    .addOnFailureListener { e ->
                        _syncState.update { it.copy(error = e) }
                        continuation.resumeWithException(e)
                    }
            }
        }
    }
}

/**
 * Helper function to merge local watchlist with cloud watchlist.
 */
private fun List<WatchlistQuote>.mergeWith(cloud: List<WatchlistQuote>): List<WatchlistQuote> {
    val cloudSymbols = cloud.map { it.symbol }.toSet()

    val uniqueLocal = this.filter { it.symbol !in cloudSymbols }
    val fromCloud = cloud.toMutableList()

    fromCloud.addAll(uniqueLocal.mapIndexed { index, quote ->
        quote.copy(order = fromCloud.size + index)
    })

    return fromCloud.sortedBy { it.order }
}