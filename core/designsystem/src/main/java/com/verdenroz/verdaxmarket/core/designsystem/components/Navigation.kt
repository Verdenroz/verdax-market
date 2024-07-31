package com.verdenroz.verdaxmarket.core.designsystem.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItemColors
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.verdenroz.verdaxmarket.core.designsystem.theme.ThemePreviews
import com.verdenroz.verdaxmarket.core.designsystem.theme.VxmTheme

/**
 * VerdaxMarket wrapper for [NavigationBar].
 * @param content The content of the navigation bar containing [VxmNavigationBarItem].
 */
@Composable
fun VxmNavigationBar(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    NavigationBar(
        modifier = modifier,
        contentColor = VxmNavigationDefaults.navigationContentColor(),
        containerColor = VxmNavigationDefaults.navigationContainerColor(),
        content = content
    )
}

/**
 * VerdaxMarket wrapper for [NavigationBarItem].
 * @param selected Whether the item is selected.
 * @param onClick The callback when the item is clicked.
 * @param enabled Whether the item is enabled.
 * @param alwaysShowLabel Whether the label should always be shown.
 * @param icon The composable icon of the item.
 * @param label The composable label of the item.
 */
@Composable
fun RowScope.VxmNavigationBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    alwaysShowLabel: Boolean = true,
    icon: @Composable () -> Unit,
    label: @Composable (() -> Unit)? = null,
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = icon,
        modifier = modifier,
        enabled = enabled,
        label = label,
        alwaysShowLabel = alwaysShowLabel,
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = VxmNavigationDefaults.navigationContentColor(),
            unselectedIconColor = VxmNavigationDefaults.navigationContentColor(),
            selectedTextColor = VxmNavigationDefaults.navigationSelectedTextColor(),
            unselectedTextColor = VxmNavigationDefaults.navigationContentColor(),
            indicatorColor = VxmNavigationDefaults.navigationIndicatorColor(),
        ),
    )
}

/**
 * VerdaxMarket wrapper for [NavigationRail].
 * @param header The optional header that holds FAB or logo
 * @param content The content of the navigation rail containing [VxmNavigationRailItem].
 */
@Composable
fun VxmNavigationRail(
    modifier: Modifier = Modifier,
    header: @Composable (ColumnScope.() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    NavigationRail(
        modifier = modifier,
        containerColor = VxmNavigationDefaults.navigationContainerColor(),
        contentColor = VxmNavigationDefaults.navigationContentColor(),
        header = header,
        content = content,
    )
}

/**
 * VerdaxMarket wrapper for [NavigationRailItem].
 * @param selected Whether the item is selected.
 * @param onClick The callback when the item is clicked.
 * @param enabled Whether the item is enabled.
 * @param alwaysShowLabel Whether the label should always be shown.
 * @param icon The composable icon of the item.
 * @param label The composable label of the item.
 */
@Composable
fun VxmNavigationRailItem(
    modifier: Modifier = Modifier,
    selected: Boolean,
    enabled: Boolean = true,
    alwaysShowLabel: Boolean = true,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    label: @Composable (() -> Unit)? = null,
) {
    NavigationRailItem(
        selected = selected,
        onClick = onClick,
        icon = icon,
        modifier = modifier,
        enabled = enabled,
        label = label,
        alwaysShowLabel = alwaysShowLabel,
        colors = NavigationRailItemDefaults.colors(
            selectedIconColor = VxmNavigationDefaults.navigationContentColor(),
            unselectedIconColor = VxmNavigationDefaults.navigationContentColor(),
            selectedTextColor = VxmNavigationDefaults.navigationSelectedTextColor(),
            unselectedTextColor = VxmNavigationDefaults.navigationContentColor(),
            indicatorColor = VxmNavigationDefaults.navigationIndicatorColor(),
        ),
    )
    Spacer(modifier = Modifier.padding(vertical = 8.dp))
}

@ThemePreviews
@Composable
fun VxmNavigationBarPreview() {
    val items = listOf("Home", "Watchlist")
    val icons = listOf(
        Icons.Rounded.Home,
        Icons.AutoMirrored.Rounded.List,
    )

    VxmTheme {
        VxmNavigationBar {
            items.forEachIndexed { index, item ->
                VxmNavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = icons[index],
                            contentDescription = item,
                        )
                    },
                    label = { Text(item) },
                    selected = index == 0,
                    onClick = { },
                )
            }
        }
    }
}

@ThemePreviews
@Composable
fun VxmNavigationRailPreview() {
    val items = listOf("Home", "Watchlist")
    val icons = listOf(
        Icons.Rounded.Home,
        Icons.AutoMirrored.Rounded.List,
    )

    VxmTheme {
        VxmNavigationRail {
            items.forEachIndexed { index, item ->
                VxmNavigationRailItem(
                    icon = {
                        Icon(
                            imageVector = icons[index],
                            contentDescription = item,
                        )
                    },
                    label = { Text(item) },
                    selected = index == 0,
                    onClick = { },
                )
            }
        }
    }
}

@Composable
fun VxmNavigationSuiteScaffold(
    navigationSuiteItems: VxmNavigationSuiteScope.() -> Unit,
    modifier: Modifier = Modifier,
    windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfo(),
    content: @Composable () -> Unit,
) {
    val layoutType = NavigationSuiteScaffoldDefaults
        .calculateFromAdaptiveInfo(windowAdaptiveInfo)
    val navigationSuiteItemColors = NavigationSuiteItemColors(
        navigationBarItemColors = NavigationBarItemDefaults.colors(
            selectedIconColor = VxmNavigationDefaults.navigationContentColor(),
            unselectedIconColor = VxmNavigationDefaults.navigationContentColor(),
            selectedTextColor = VxmNavigationDefaults.navigationSelectedTextColor(),
            unselectedTextColor = VxmNavigationDefaults.navigationContentColor(),
            indicatorColor = VxmNavigationDefaults.navigationIndicatorColor(),
        ),
        navigationRailItemColors = NavigationRailItemDefaults.colors(
            selectedIconColor = VxmNavigationDefaults.navigationContentColor(),
            unselectedIconColor = VxmNavigationDefaults.navigationContentColor(),
            selectedTextColor = VxmNavigationDefaults.navigationSelectedTextColor(),
            unselectedTextColor = VxmNavigationDefaults.navigationContentColor(),
            indicatorColor = VxmNavigationDefaults.navigationIndicatorColor(),
        ),
        navigationDrawerItemColors = NavigationDrawerItemDefaults.colors(
            selectedIconColor = VxmNavigationDefaults.navigationContentColor(),
            unselectedIconColor = VxmNavigationDefaults.navigationContentColor(),
            selectedTextColor = VxmNavigationDefaults.navigationSelectedTextColor(),
            unselectedTextColor = VxmNavigationDefaults.navigationContentColor(),
        ),
    )

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            VxmNavigationSuiteScope(
                navigationSuiteScope = this,
                navigationSuiteItemColors = navigationSuiteItemColors,
            ).run(navigationSuiteItems)
        },
        layoutType = layoutType,
        containerColor = Color.Transparent,
        navigationSuiteColors = NavigationSuiteDefaults.colors(
            navigationBarContentColor = VxmNavigationDefaults.navigationContentColor(),
            navigationBarContainerColor = VxmNavigationDefaults.navigationContainerColor(),
            navigationRailContentColor = VxmNavigationDefaults.navigationContentColor(),
            navigationRailContainerColor = VxmNavigationDefaults.navigationContainerColor(),
        ),
        modifier = modifier,
        content = content
    )
}


class VxmNavigationSuiteScope internal constructor(
    private val navigationSuiteScope: NavigationSuiteScope,
    private val navigationSuiteItemColors: NavigationSuiteItemColors,
) {
    fun item(
        selected: Boolean,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        icon: @Composable () -> Unit,
        label: @Composable (() -> Unit)? = null,
    ) = navigationSuiteScope.item(
        selected = selected,
        onClick = onClick,
        icon = icon,
        label = label,
        colors = navigationSuiteItemColors,
        modifier = modifier,
    )
}


object VxmNavigationDefaults {
    @Composable
    fun navigationContainerColor() = MaterialTheme.colorScheme.surfaceContainerHighest

    @Composable
    fun navigationContentColor() = MaterialTheme.colorScheme.onSurface

    @Composable
    fun navigationSelectedTextColor() = MaterialTheme.colorScheme.secondary

    @Composable
    fun navigationIndicatorColor() = MaterialTheme.colorScheme.tertiaryContainer
}