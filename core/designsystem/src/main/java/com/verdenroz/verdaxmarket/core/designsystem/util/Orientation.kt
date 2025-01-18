package com.verdenroz.verdaxmarket.core.designsystem.util

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.window.core.layout.WindowHeightSizeClass
import androidx.window.core.layout.WindowWidthSizeClass

/**
 * Returns true if the device is a tablet.
 */
@Composable
fun isTablet(): Boolean {
    val windowClass = currentWindowAdaptiveInfo().windowSizeClass
    return windowClass.windowWidthSizeClass == WindowWidthSizeClass.MEDIUM
            || windowClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED
}

/**
 * Returns true if the device is in landscape mode.
 */
@Composable
fun isLandscape(): Boolean {
    val windowClass = currentWindowAdaptiveInfo().windowSizeClass
    return windowClass.windowHeightSizeClass == WindowHeightSizeClass.COMPACT
            || windowClass.windowWidthSizeClass == WindowWidthSizeClass.MEDIUM
            || windowClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED
}

/**
 * Returns true if the device is in portrait mode.
 */
@Composable
fun isPortrait(): Boolean {
    val windowClass = currentWindowAdaptiveInfo().windowSizeClass
    return windowClass.windowHeightSizeClass != WindowHeightSizeClass.COMPACT
}