package com.verdenroz.core.sync

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.verdenroz.core.datastore.UserSettingsStore
import com.verdenroz.verdaxmarket.core.common.dispatchers.Dispatcher
import com.verdenroz.verdaxmarket.core.common.dispatchers.FinanceQueryDispatchers
import com.verdenroz.verdaxmarket.core.common.dispatchers.di.ApplicationScope
import com.verdenroz.verdaxmarket.core.data.model.asExternalModel
import com.verdenroz.verdaxmarket.core.data.repository.WatchlistRepository
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
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncManager @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val userSettingsStore: UserSettingsStore,
    private val watchlistRepository: WatchlistRepository,
    @Dispatcher(FinanceQueryDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    @ApplicationScope private val scope: CoroutineScope
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
        val cloudSettings = getSettings()
        val localSettings = userSettingsStore.userSettings.first()

        if (cloudSettings != null && !localSettings.isOnboardingComplete) {
            userSettingsStore.updateSettings(cloudSettings)
        } else if (localSettings.isOnboardingComplete) {
            syncSettings(localSettings)
        }
    }

    /**
     * Syncs the user settings with the cloud.
     */
    private suspend fun syncWatchlist() {
        val localWatchlist = watchlistRepository.getWatchlist().map { it.asExternalModel() }
        val cloudWatchlist = getWatchlist()

        when {
            cloudWatchlist != null && localWatchlist.isEmpty() -> {
                watchlistRepository.updateWatchlist(cloudWatchlist)
            }
            cloudWatchlist != null -> {
                val mergedWatchlist = localWatchlist.mergeWith(cloudWatchlist)
                watchlistRepository.updateWatchlist(mergedWatchlist)
            }
            localWatchlist.isNotEmpty() -> {
                syncWatchlist(localWatchlist)
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
                            syncSettings(settings)
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
                            syncWatchlist(watchlist)
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
     * Syncs the user settings with the cloud.
     */
    private suspend fun syncWatchlist(watchlist: List<WatchlistQuote>) {
        currentUserId?.let { userId ->
            val watchlistData = watchlist.map { quote ->
                mapOf(
                    "symbol" to quote.symbol,
                    "name" to quote.name,
                    "logo" to quote.logo,
                    "order" to quote.order
                )
            }
            firestore.collection("users")
                .document(userId)
                .collection("watchlist")
                .document("symbols")
                .set(mapOf("items" to watchlistData))
                .await()
        }
    }

    /**
     * Gets the watchlist from the cloud.
     */
    private suspend fun getWatchlist(): List<WatchlistQuote>? {
        return currentUserId?.let { userId ->
            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("watchlist")
                .document("symbols")
                .get()
                .await()

            @Suppress("UNCHECKED_CAST")
            val items = snapshot.get("items") as? List<Map<String, Any>> ?: return null

            items.map { item ->
                WatchlistQuote(
                    symbol = item["symbol"] as String,
                    name = item["name"] as String,
                    logo = item["logo"] as? String,
                    order = (item["order"] as Long).toInt(),
                    price = null,
                    change = null,
                    percentChange = null
                )
            }
        }
    }

    /**
     * Syncs the user settings with the cloud.
     */
    private suspend fun syncSettings(settings: UserSetting) {
        currentUserId?.let { userId ->
            val settingsData = mapOf(
                "themePreference" to settings.themePreference.name,
                "notificationsEnabled" to settings.notificationsEnabled,
                "hintsEnabled" to settings.hintsEnabled,
                "showMarketHours" to settings.showMarketHours,
                "enableAnonymousAnalytics" to settings.enableAnonymousAnalytics,
                "isOnboardingComplete" to settings.isOnboardingComplete
            )
            firestore.collection("users")
                .document(userId)
                .collection("settings")
                .document("user_settings")
                .set(settingsData)
                .await()
        }
    }

    /**
     * Gets the user settings from the cloud.
     */
    private suspend fun getSettings(): UserSetting? {
        return currentUserId?.let { userId ->
            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("settings")
                .document("user_settings")
                .get()
                .await()

            if (!snapshot.exists()) return null

            UserSetting(
                themePreference = ThemePreference.valueOf(
                    snapshot.getString("themePreference") ?: ThemePreference.SYSTEM.name
                ),
                notificationsEnabled = snapshot.getBoolean("notificationsEnabled") != false,
                hintsEnabled = snapshot.getBoolean("hintsEnabled") != false,
                showMarketHours = snapshot.getBoolean("showMarketHours") != false,
                enableAnonymousAnalytics = snapshot.getBoolean("enableAnonymousAnalytics") == true,
                isOnboardingComplete = snapshot.getBoolean("isOnboardingComplete") == true
            )
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