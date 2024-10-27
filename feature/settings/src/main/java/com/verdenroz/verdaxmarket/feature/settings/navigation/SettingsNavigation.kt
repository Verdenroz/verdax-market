package com.verdenroz.verdaxmarket.feature.settings.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.verdenroz.verdaxmarket.feature.settings.SettingsRoute

const val SETTINGS_ROUTE = "settings_route"

fun NavController.navigateToSettings(navOptions: NavOptions? = null) = navigate(SETTINGS_ROUTE, navOptions)

fun NavGraphBuilder.settingsScreen(
    onNavigateBack: () -> Unit,
) {
    composable(route = SETTINGS_ROUTE) {
        SettingsRoute(
            onNavigateBack = onNavigateBack,
        )
    }
}