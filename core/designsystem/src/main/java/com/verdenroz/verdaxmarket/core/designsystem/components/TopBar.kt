package com.verdenroz.verdaxmarket.core.designsystem.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.verdenroz.verdaxmarket.core.designsystem.icons.VxmIcons
import com.verdenroz.verdaxmarket.core.designsystem.theme.ThemePreviews
import com.verdenroz.verdaxmarket.core.designsystem.theme.VxmTheme

/**
 * VxmTopBar is a wrapper around [CenterAlignedTopAppBar] with a smaller height.
 * @param modifier Modifier to be applied to the layout.
 * @param title Title of the top bar.
 * @param navigationIcon Icon to be displayed on the left side of the top bar.
 * @param actions Actions to be displayed on the right side of the top bar.
 * @param colors Colors to be applied to the top bar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VxmCenterTopBar(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit = {},
    navigationIcon: @Composable (() -> Unit) = {},
    actions: @Composable (RowScope.() -> Unit) = {},
    colors: TopAppBarColors = TopAppBarDefaults.centerAlignedTopAppBarColors(
        containerColor = MaterialTheme.colorScheme.surface,
        navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        actionIconContentColor = MaterialTheme.colorScheme.onSurface,
    ),
) {
    CenterAlignedTopAppBar(
        title = title,
        navigationIcon = navigationIcon,
        actions = actions,
        expandedHeight = 48.dp,
        colors = colors,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@ThemePreviews
@Composable
private fun PreviewVxmCenterTopBar() {
    VxmTheme {
        VxmCenterTopBar(
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