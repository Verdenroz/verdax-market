package com.verdenroz.verdaxmarket.ui

import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarDuration.Indefinite
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import com.verdenroz.verdaxmarket.R
import com.verdenroz.verdaxmarket.core.designsystem.components.VxmNavigationSuiteScaffold
import com.verdenroz.verdaxmarket.navigation.VxmNavHost

@Composable
fun VxmApp(
    appState: VxmAppState,
    modifier: Modifier = Modifier,
    windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfo(),
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val isOffline by appState.isOffline.collectAsStateWithLifecycle()

    // If user is not connected to the internet show a snack bar to inform them.
    val notConnectedMessage = stringResource(R.string.not_connected)
    LaunchedEffect(isOffline) {
        if (isOffline) {
            snackbarHostState.showSnackbar(
                message = notConnectedMessage,
                duration = Indefinite,
            )
        }
    }

    VxmAppContent(
        appState = appState,
        snackbarHostState = snackbarHostState,
        windowAdaptiveInfo = windowAdaptiveInfo,
        modifier = modifier,
    )
}

@Composable
internal fun VxmAppContent(
    appState: VxmAppState,
    snackbarHostState: SnackbarHostState,
    windowAdaptiveInfo: WindowAdaptiveInfo,
    modifier: Modifier,
) {
    val currentDestination = appState.currentDestination

    VxmNavigationSuiteScaffold(
        modifier = modifier,
        windowAdaptiveInfo = windowAdaptiveInfo,
        navigationSuiteItems = {
            appState.topLevelDestinations.forEach { destination ->

                // see what top level destination is selected based on the current destination
                val selected = currentDestination?.hierarchy?.any {
                    it.route?.contains(destination.name, ignoreCase = true)
                        ?: false
                } ?: false

                item(
                    selected = selected,
                    onClick = { appState.navigateToTopLevelDestination(destination) },
                    icon = {
                        Icon(
                            imageVector = destination.icon,
                            contentDescription = destination.name
                        )
                    },
                    label = { Text(text = stringResource(destination.title)) },
                )
            }
        }
    ) {
        VxmNavHost(
            appState = appState,
            snackbarHostState = snackbarHostState,
        )
    }
}