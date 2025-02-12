package com.verdenroz.verdaxmarket.feature.search.navigation

import androidx.compose.material3.SnackbarDuration
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.verdenroz.verdaxmarket.feature.search.SearchRoute

const val SEARCH_ROUTE = "search_route"

fun NavController.navigateToSearch(navOptions: NavOptions) = navigate(SEARCH_ROUTE, navOptions)

fun NavGraphBuilder.searchScreen(
    onNavigateToQuote: (String) -> Unit,
    onShowSnackbar: suspend (String, String?, SnackbarDuration) -> Boolean,
) {
    composable(route = SEARCH_ROUTE) {
        SearchRoute(
            onNavigateToQuote = onNavigateToQuote,
            onShowSnackbar = onShowSnackbar
        )
    }
}