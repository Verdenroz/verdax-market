package com.verdenroz.verdaxmarket.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration.Indefinite
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
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
import com.verdenroz.verdaxmarket.core.designsystem.components.VxmSnackbarHost
import com.verdenroz.verdaxmarket.core.designsystem.components.VxmTopBar
import com.verdenroz.verdaxmarket.core.designsystem.icons.VxmIcons
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
        onNavigateToSettings = appState::navigateToSettings,
        modifier = modifier,
    )
}

@Composable
internal fun VxmAppContent(
    appState: VxmAppState,
    snackbarHostState: SnackbarHostState,
    windowAdaptiveInfo: WindowAdaptiveInfo,
    onNavigateToSettings: () -> Unit,
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
        Scaffold(
            snackbarHost = {
                VxmSnackbarHost(hostState = snackbarHostState)
            },
            topBar = {
                if (appState.isTopLevelDestination) {
                    VxmTopBar(
                        title = {
                            Text(text = stringResource(R.string.app_name))
                        },
                        actions = {
                            IconButton(onClick = onNavigateToSettings) {
                                Icon(
                                    imageVector = VxmIcons.Settings,
                                    contentDescription = stringResource(id = R.string.settings)
                                )
                            }
                        }
                    )
                }
            }

        ) { padding ->
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .consumeWindowInsets(padding)
                    .windowInsetsPadding(
                        WindowInsets.safeDrawing.only(
                            WindowInsetsSides.Horizontal,
                        ),
                    ),
            ) {
                VxmNavHost(
                    appState = appState,
                    onShowSnackbar = { message, action, duration ->
                        snackbarHostState.showSnackbar(
                            message = message,
                            actionLabel = action,
                            duration = duration,
                        ) == SnackbarResult.ActionPerformed
                    },
                )
            }
        }
    }
}