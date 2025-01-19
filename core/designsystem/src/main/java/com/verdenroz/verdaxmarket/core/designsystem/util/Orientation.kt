package com.verdenroz.verdaxmarket.core.designsystem.util

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.window.core.layout.WindowHeightSizeClass
import androidx.window.core.layout.WindowWidthSizeClass

/**
 * Returns true if the device is a tablet, based on screen width.
 * Tablets are considered to have MEDIUM or EXPANDED width regardless of orientation.
 */
@Composable
fun isTablet(): Boolean {
    val windowClass = currentWindowAdaptiveInfo().windowSizeClass
    return windowClass.windowWidthSizeClass == WindowWidthSizeClass.MEDIUM
            || windowClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED
}

/**
 * Returns true if the device is in landscape orientation.
 * Landscape is when height is COMPACT or width is MEDIUM/EXPANDED
 */
@Composable
fun isLandscape(): Boolean {
    val windowClass = currentWindowAdaptiveInfo().windowSizeClass
    return windowClass.windowHeightSizeClass == WindowHeightSizeClass.COMPACT
}

/**
 * Returns true if the device is in portrait orientation.
 */
@Composable
fun isPortrait(): Boolean {
    return !isLandscape()
}