package com.verdenroz.verdaxmarket.feature.home.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.verdenroz.verdaxmarket.feature.home.HomeRoute

private const val HOME_ROUTE = "home_route"

fun NavController.navigateToHome(navOptions: NavOptions) = navigate(HOME_ROUTE, navOptions)

fun NavGraphBuilder.homeScreen(
    navController: NavController,
    snackbarHostState: SnackbarHostState
) {
    composable(route = HOME_ROUTE) {
        HomeRoute(
            navController = navController,
            snackbarHostState = snackbarHostState
        )
    }
}
