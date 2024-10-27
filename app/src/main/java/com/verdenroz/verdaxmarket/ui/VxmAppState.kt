package com.verdenroz.verdaxmarket.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.verdenroz.verdaxmarket.core.data.utils.MarketMonitor
import com.verdenroz.verdaxmarket.core.data.utils.NetworkMonitor
import com.verdenroz.verdaxmarket.feature.home.navigation.HOME_ROUTE
import com.verdenroz.verdaxmarket.feature.home.navigation.navigateToHome
import com.verdenroz.verdaxmarket.feature.search.navigation.SEARCH_ROUTE
import com.verdenroz.verdaxmarket.feature.search.navigation.navigateToSearch
import com.verdenroz.verdaxmarket.feature.settings.navigation.navigateToSettings
import com.verdenroz.verdaxmarket.feature.watchlist.navigation.WATCHLIST_ROUTE
import com.verdenroz.verdaxmarket.feature.watchlist.navigation.navigateToWatchlist
import com.verdenroz.verdaxmarket.navigation.TopLevelDestination
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@Composable
fun rememberVxmAppState(
    networkMonitor: NetworkMonitor,
    marketMonitor: MarketMonitor,
    navController: NavHostController = rememberNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
): VxmAppState {
    return remember(
        navController,
        coroutineScope,
        networkMonitor
    ) {
        VxmAppState(
            navController = navController,
            coroutineScope = coroutineScope,
            networkMonitor = networkMonitor,
            marketMonitor = marketMonitor,
        )
    }
}

@Stable
class VxmAppState(
    val navController: NavHostController,
    coroutineScope: CoroutineScope,
    networkMonitor: NetworkMonitor,
    marketMonitor: MarketMonitor,
) {

    val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination

    val isTopLevelDestination: Boolean
        @Composable get() = when (currentDestination?.route) {
            HOME_ROUTE,
            SEARCH_ROUTE,
            WATCHLIST_ROUTE -> true
            else -> false
        }

    val topLevelDestinations: List<TopLevelDestination> = TopLevelDestination.entries

    val isOffline: StateFlow<Boolean> = networkMonitor.isOnline
        .map(Boolean::not)
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )

    val isMarketOpen: StateFlow<Boolean> = marketMonitor.isMarketOpen
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )

    /**
     * UI logic for navigating to a top level destination in the app. Top level destinations have
     * only one copy of the destination of the back stack, and save and restore state whenever you
     * navigate to and from it.
     *
     * Copied from src/main/kotlin/com/google/samples/apps/nowinandroid/ui/NiaAppState.kt
     *
     * @param topLevelDestination: The destination the app needs to navigate to.
     * @see <a href="https://github.com/android/nowinandroid/blob/main/app/src/main/kotlin/com/google/samples/apps/nowinandroid/ui/NiaAppState.kt">NiaAppState.kt</a>
     */
    fun navigateToTopLevelDestination(topLevelDestination: TopLevelDestination) {
        val topLevelNavOptions = navOptions {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            // Avoid multiple copies of the same destination when
            // reselecting the same item
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
            restoreState = true
        }

        when (topLevelDestination) {
            TopLevelDestination.HOME -> navController.navigateToHome(topLevelNavOptions)
            TopLevelDestination.SEARCH -> navController.navigateToSearch(topLevelNavOptions)
            TopLevelDestination.WATCHLIST -> navController.navigateToWatchlist(topLevelNavOptions)
        }

        // Pop back to the top-level destination if currently on a QuoteScreen
        while (navController.currentDestination?.route?.startsWith("quote_route") == true) {
            navController.popBackStack()
        }
    }

    fun navigateToSettings() {
        navController.navigateToSettings()
    }

}