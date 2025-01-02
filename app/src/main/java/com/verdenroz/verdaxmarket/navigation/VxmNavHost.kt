package com.verdenroz.verdaxmarket.navigation

import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import com.verdenroz.verdaxmarket.feature.home.navigation.HOME_ROUTE
import com.verdenroz.verdaxmarket.feature.home.navigation.homeScreen
import com.verdenroz.verdaxmarket.feature.quotes.navigation.navigateToQuote
import com.verdenroz.verdaxmarket.feature.quotes.navigation.quotesScreen
import com.verdenroz.verdaxmarket.feature.search.navigation.searchScreen
import com.verdenroz.verdaxmarket.feature.settings.navigation.settingsScreen
import com.verdenroz.verdaxmarket.feature.watchlist.navigation.navigateToEditWatchlist
import com.verdenroz.verdaxmarket.feature.watchlist.navigation.watchlistScreen
import com.verdenroz.verdaxmarket.ui.VxmAppState

@Composable
fun VxmNavHost(
    appState: VxmAppState,
    onShowSnackbar: suspend (String, String?, SnackbarDuration) -> Boolean,
    startDestination: String = HOME_ROUTE,
) {
    val navController = appState.navController
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        homeScreen(
            onNavigateToQuote = navController::navigateToQuote,
            onShowSnackbar = onShowSnackbar,
        )
        searchScreen(
            onNavigateToQuote = navController::navigateToQuote,
            onShowSnackbar = onShowSnackbar,
        )
        watchlistScreen(
            onNavigateToEdit = navController::navigateToEditWatchlist,
            onNavigateBack = navController::popBackStack,
            onNavigateToQuote = navController::navigateToQuote,
            onShowSnackbar = onShowSnackbar,
        )
        settingsScreen(
            onNavigateBack = navController::popBackStack,
            onShowSnackbar = onShowSnackbar,
        )
        quotesScreen(
            onNavigateBack = navController::popBackStack,
            onNavigateToQuote = navController::navigateToQuote,
        )
    }
}