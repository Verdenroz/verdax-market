package com.verdenroz.verdaxmarket

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.verdenroz.core.sync.SyncManager
import com.verdenroz.verdaxmarket.core.data.repository.RecentSearchRepository
import com.verdenroz.verdaxmarket.core.data.repository.WatchlistRepository
import com.verdenroz.verdaxmarket.core.data.utils.MarketMonitor
import com.verdenroz.verdaxmarket.core.data.utils.NetworkMonitor
import com.verdenroz.verdaxmarket.core.designsystem.theme.VxmTheme
import com.verdenroz.verdaxmarket.core.model.ThemePreference
import com.verdenroz.verdaxmarket.ui.VxmApp
import com.verdenroz.verdaxmarket.ui.rememberVxmAppState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var networkMonitor: NetworkMonitor

    @Inject
    lateinit var marketMonitor: MarketMonitor

    @Inject
    lateinit var watchlistRepository: WatchlistRepository

    @Inject
    lateinit var recentSearchRepository: RecentSearchRepository

    @Inject
    lateinit var syncManager: SyncManager

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        var uiState: MainActivityUiState by mutableStateOf(MainActivityUiState.Loading)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState
                    .onEach { uiState = it }
                    .collect { }

                // Start collecting the watchlist quotes
                watchlistRepository.quotes.collect { }

                // Start collecting the recent quotes
                recentSearchRepository.recentQuotes.collect { }

                launch {
                    // Start collecting the sync state
                    syncManager.syncState
                        .onEach { syncState ->
                            syncState.error?.let { error ->
                                Log.e("SyncManager", "Sync error: $error")
                            }
                        }
                        .collect { }
                }
            }
        }

        splashScreen.setKeepOnScreenCondition {
            when (uiState) {
                MainActivityUiState.Loading -> true
                is MainActivityUiState.Success -> false
            }
        }

        enableEdgeToEdge()
        setContent {
            val isDarkTheme = shouldUseDarkTheme(uiState = uiState)
            val showMarketHours = shouldShowMarketHOurs(uiState = uiState)

            DisposableEffect(isDarkTheme) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(
                        android.graphics.Color.TRANSPARENT,
                        android.graphics.Color.TRANSPARENT,
                    ) { isDarkTheme },
                    navigationBarStyle = SystemBarStyle.auto(
                        lightScrim,
                        darkScrim,
                    ) { isDarkTheme },
                )
                onDispose {}
            }

            val appState = rememberVxmAppState(
                networkMonitor = networkMonitor,
                marketMonitor = marketMonitor,
            )

            VxmTheme(isDarkTheme = isDarkTheme) {
                VxmApp(
                    appState = appState,
                    showMarketHours = showMarketHours
                )
            }
        }
    }
}

@Composable
private fun shouldUseDarkTheme(uiState: MainActivityUiState): Boolean {
    return when (uiState) {
        is MainActivityUiState.Success -> when (uiState.userSetting.themePreference) {
            ThemePreference.SYSTEM -> isSystemInDarkTheme()
            ThemePreference.LIGHT -> false
            ThemePreference.DARK -> true
        }

        is MainActivityUiState.Loading -> isSystemInDarkTheme()
    }
}

@Composable
private fun shouldShowMarketHOurs(uiState: MainActivityUiState): Boolean {
    return when (uiState) {
        is MainActivityUiState.Success -> uiState.userSetting.showMarketHours
        is MainActivityUiState.Loading -> false
    }
}

/**
 * The default light scrim, as defined by androidx and the platform:
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:activity/activity/src/main/java/androidx/activity/EdgeToEdge.kt;l=35-38;drc=27e7d52e8604a080133e8b842db10c89b4482598
 */
private val lightScrim = android.graphics.Color.argb(0xe6, 0xFF, 0xFF, 0xFF)

/**
 * The default dark scrim, as defined by androidx and the platform:
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:activity/activity/src/main/java/androidx/activity/EdgeToEdge.kt;l=40-44;drc=27e7d52e8604a080133e8b842db10c89b4482598
 */
private val darkScrim = android.graphics.Color.argb(0x80, 0x1b, 0x1b, 0x1b)