package com.verdenroz.verdaxmarket.core.designsystem.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.verdenroz.verdaxmarket.core.designsystem.icons.VxmIcons
import com.verdenroz.verdaxmarket.core.designsystem.theme.ThemePreviews
import com.verdenroz.verdaxmarket.core.designsystem.theme.VxmTheme

/**
 * VxmTopBar is a wrapper around [CenterAlignedTopAppBar] with a smaller height.
 * Currently, no custom theming applied, but it can be added in the future for
 * all the top bars in the app.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VxmTopBar(
    title: @Composable () -> Unit = {},
    navigationIcon: @Composable (() -> Unit) = {},
    actions: @Composable (RowScope.() -> Unit) = {}
) {
    CenterAlignedTopAppBar(
        title = title,
        navigationIcon = navigationIcon,
        actions = actions,
        expandedHeight = 48.dp
    )
}

@ThemePreviews
@Composable
private fun PreviewVxmTopBar() {
    VxmTheme {
        VxmTopBar(
            title = {
                Text(
                    text = "Title",
                    textAlign = TextAlign.Center
                )
            },
            navigationIcon = {
                Icon(
                    imageVector = VxmIcons.ArrowBack,
                    contentDescription = null,
                )
            },
            actions = {
                Icon(
                    imageVector = VxmIcons.Add,
                    contentDescription = null,
                )
            }
        )
    }
}