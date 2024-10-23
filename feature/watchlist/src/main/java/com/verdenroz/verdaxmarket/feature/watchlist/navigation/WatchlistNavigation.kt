package com.verdenroz.verdaxmarket.feature.watchlist.navigation

import androidx.compose.material3.SnackbarDuration
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.verdenroz.verdaxmarket.feature.watchlist.WatchlistRoute

private const val WATCHLIST_ROUTE = "watchlist_route"

fun NavController.navigateToWatchlist(navOptions: NavOptions) = navigate(WATCHLIST_ROUTE, navOptions)


fun NavGraphBuilder.watchlistScreen(
    onNavigateToQuote: (String) -> Unit,
    onShowSnackbar: suspend (String, String?, SnackbarDuration) -> Boolean,
    ) {
    composable(route = WATCHLIST_ROUTE) {
        WatchlistRoute(
            onNavigateToQuote = onNavigateToQuote,
            onShowSnackbar = onShowSnackbar
        )
    }
}
