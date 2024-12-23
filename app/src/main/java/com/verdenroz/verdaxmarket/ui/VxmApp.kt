package com.verdenroz.verdaxmarket.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import com.verdenroz.verdaxmarket.R
import com.verdenroz.verdaxmarket.core.designsystem.components.VxmNavigationSuiteScaffold
import com.verdenroz.verdaxmarket.core.designsystem.components.VxmSnackbarHost
import com.verdenroz.verdaxmarket.core.designsystem.components.VxmTopAppBar
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun VxmAppContent(
    appState: VxmAppState,
    snackbarHostState: SnackbarHostState,
    windowAdaptiveInfo: WindowAdaptiveInfo,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier,
) {
    val currentDestination = appState.currentDestination
    val marketHours by appState.marketHours.collectAsStateWithLifecycle()

    var isHoursClicked by remember { mutableStateOf(false) }
    val topBarHeight by animateDpAsState(
        if (isHoursClicked) 128.dp else 80.dp,
        label = "topBarHeight"
    )

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
                    VxmTopAppBar(
                        navigationIcon = {
                            MarketHoursCard(
                                marketHours = marketHours,
                                expanded = isHoursClicked,
                                modifier = Modifier
                                    .padding(horizontal = 8.dp)
                                    .pointerInput(Unit) {
                                        detectTapGestures(
                                            onTap = { isHoursClicked = !isHoursClicked }
                                        )
                                    },
                            )
                        },
                        actions = {
                            IconButton(onClick = onNavigateToSettings) {
                                Icon(
                                    imageVector = VxmIcons.Settings,
                                    contentDescription = stringResource(id = R.string.settings)
                                )
                            }
                        },
                        expandedHeight = topBarHeight,
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