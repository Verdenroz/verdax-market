package com.verdenroz.verdaxmarket.feature.watchlist.navigation

import androidx.compose.material3.SnackbarDuration
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.verdenroz.verdaxmarket.feature.watchlist.EditWatchlistRoute
import com.verdenroz.verdaxmarket.feature.watchlist.WatchlistRoute

const val WATCHLIST_ROUTE = "watchlist_route"
const val EDIT_WATCHLIST_ROUTE = "edit_watchlist_route"

fun NavController.navigateToWatchlist(navOptions: NavOptions) = navigate(WATCHLIST_ROUTE, navOptions)

fun NavController.navigateToEditWatchlist(navOptions: NavOptions? = null) = navigate(EDIT_WATCHLIST_ROUTE, navOptions)


fun NavGraphBuilder.watchlistScreen(
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (NavOptions?) -> Unit,
    onNavigateToQuote: (String) -> Unit,
    onShowSnackbar: suspend (String, String?, SnackbarDuration) -> Boolean,
    ) {
    composable(route = WATCHLIST_ROUTE) {
        WatchlistRoute(
            onNavigateToEdit = onNavigateToEdit,
            onNavigateToQuote = onNavigateToQuote,
            onShowSnackbar = onShowSnackbar
        )
    }
    composable(route = EDIT_WATCHLIST_ROUTE) {
        EditWatchlistRoute(
            onNavigateBack = onNavigateBack,
        )
    }
}
