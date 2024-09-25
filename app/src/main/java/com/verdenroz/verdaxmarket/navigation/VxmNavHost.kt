package com.verdenroz.verdaxmarket.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import com.verdenroz.verdaxmarket.feature.home.navigation.HOME_ROUTE
import com.verdenroz.verdaxmarket.feature.home.navigation.homeScreen
import com.verdenroz.verdaxmarket.feature.quotes.navigation.quoteScreen
import com.verdenroz.verdaxmarket.feature.search.navigation.searchScreen
import com.verdenroz.verdaxmarket.feature.watchlist.navigation.watchlistScreen
import com.verdenroz.verdaxmarket.ui.VxmAppState

@Composable
fun VxmNavHost(
    appState: VxmAppState,
    snackbarHostState: SnackbarHostState,
    startDestination: String = HOME_ROUTE
) {
    val navController = appState.navController

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        homeScreen(
            navController = navController,
            snackbarHostState = snackbarHostState
        )
        searchScreen(
            navController = navController,
        )
        watchlistScreen(
            navController = navController,
            snackbarHostState = snackbarHostState
        )
        quoteScreen(
            navController = navController,
            snackbarHostState = snackbarHostState
        )
    }
}