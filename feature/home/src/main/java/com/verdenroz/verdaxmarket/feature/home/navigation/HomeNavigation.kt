package com.verdenroz.verdaxmarket.feature.home.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.verdenroz.verdaxmarket.feature.home.HomeRoute

const val HOME_ROUTE = "home_route"

fun NavController.navigateToHome(navOptions: NavOptions) = navigate(HOME_ROUTE, navOptions)

fun NavGraphBuilder.homeScreen(
    onQuoteClick: (String) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    composable(route = HOME_ROUTE) {
        HomeRoute(
            onQuoteClick = onQuoteClick,
            snackbarHostState = snackbarHostState
        )
    }
}
