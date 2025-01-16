package com.verdenroz.verdaxmarket.ui

import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarDuration.Indefinite
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import com.verdenroz.verdaxmarket.R
import com.verdenroz.verdaxmarket.core.designsystem.components.VxmNavigationSuiteScaffold
import com.verdenroz.verdaxmarket.core.designsystem.components.VxmSnackbarHost
import com.verdenroz.verdaxmarket.core.designsystem.components.VxmTopAppBar
import com.verdenroz.verdaxmarket.core.designsystem.icons.VxmIcons
import com.verdenroz.verdaxmarket.feature.settings.navigation.navigateToSettings
import com.verdenroz.verdaxmarket.navigation.VxmNavHost
import kotlin.system.exitProcess

@Composable
fun VxmApp(
    appState: VxmAppState,
    showMarketHours: Boolean,
    modifier: Modifier = Modifier,
    windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfo(),
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    val syncState by appState.syncState.collectAsStateWithLifecycle()
    val isOffline by appState.isOffline.collectAsStateWithLifecycle()

    val notConnectedMessage = stringResource(R.string.not_connected)
    val syncError = stringResource(R.string.sync_error)
    val syncRetry = stringResource(R.string.retry)
    val exitToast = stringResource(R.string.exit_toast)

    var showExitToast by remember { mutableStateOf(false) }
    var lastBackPressTime by remember { mutableLongStateOf(0L) }

    // Don't enable the BackHandler for the search route since it has its own back handling
    BackHandler {
        val currentTime = System.currentTimeMillis()

        when {
            // If we have a previous destination in the back stack
            appState.navController.previousBackStackEntry != null -> {
                appState.navController.popBackStack()
            }
            // If we are at the top level destination
            else -> {
                if (currentTime - lastBackPressTime < 2000) {
                    exitProcess(0)
                } else {
                    showExitToast = true
                    lastBackPressTime = currentTime
                }
            }
        }
    }

    // Show toast when needed
    LaunchedEffect(showExitToast) {
        if (showExitToast) {
            Toast.makeText(
                context,
                exitToast,
                Toast.LENGTH_SHORT
            ).show()
            showExitToast = false
        }
    }

    // If user is not connected to the internet show a snack bar to inform them.
    LaunchedEffect(isOffline) {
        if (isOffline) {
            snackbarHostState.showSnackbar(
                message = notConnectedMessage,
                duration = Indefinite,
            )
        }
    }

    // If there is a sync error show a snack bar to inform the user and allow them to retry.
    LaunchedEffect(syncState) {
        if (syncState.error != null) {
            val result = snackbarHostState.showSnackbar(
                message = syncError,
                actionLabel = syncRetry,
                duration = SnackbarDuration.Long
            )
            if (result == SnackbarResult.ActionPerformed) {
                appState.syncManager.retrySync()
            }
        }
    }

    VxmAppContent(
        appState = appState,
        showMarketHours = showMarketHours,
        snackbarHostState = snackbarHostState,
        windowAdaptiveInfo = windowAdaptiveInfo,
        onNavigateToSettings = appState.navController::navigateToSettings,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun VxmAppContent(
    appState: VxmAppState,
    showMarketHours: Boolean,
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
                    it.route?.contains(destination.name, ignoreCase = true) == true
                } == true

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
                            if (showMarketHours) {
                                MarketHoursCard(
                                    marketHours = marketHours,
                                    expanded = isHoursClicked,
                                    modifier = Modifier
                                        .padding(horizontal = 8.dp)
                                        .pointerInput(Unit) {
                                            detectTapGestures(
                                                onTap = { isHoursClicked = !isHoursClicked }
                                            )
                                        }
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = onNavigateToSettings) {
                                Icon(
                                    imageVector = VxmIcons.Settings,
                                    contentDescription = stringResource(id = R.string.settings)
                                )
                            }
                        },
                        expandedHeight = if (showMarketHours) topBarHeight else 48.dp,
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