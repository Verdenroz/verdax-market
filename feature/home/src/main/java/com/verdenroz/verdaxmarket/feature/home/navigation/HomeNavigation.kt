package com.verdenroz.verdaxmarket.feature.home.navigation

import androidx.compose.material3.SnackbarDuration
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.verdenroz.verdaxmarket.feature.home.HomeRoute

const val HOME_ROUTE = "home_route"

fun NavController.navigateToHome(navOptions: NavOptions) = navigate(HOME_ROUTE, navOptions)

fun NavGraphBuilder.homeScreen(
    onNavigateToQuote: (String) -> Unit,
    onShowSnackbar: suspend (String, String?, SnackbarDuration) -> Boolean,
) {
    composable(route = HOME_ROUTE) {
        HomeRoute(
            onNavigateToQuote = onNavigateToQuote,
            onShowSnackbar = onShowSnackbar,
        )
    }
}
