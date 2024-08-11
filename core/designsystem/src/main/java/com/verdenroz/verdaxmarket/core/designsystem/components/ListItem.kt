package com.verdenroz.verdaxmarket.core.designsystem.components

import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.verdenroz.verdaxmarket.core.designsystem.theme.ThemePreviews

/**
 * VerdaxMarket wrapper around [ListItem] with custom styling.
 * @param modifier The modifier to be applied to the layout.
 * @param headlineContent The content of the headline.
 * @param supportingContent The optional content of the supporting text.
 * @param trailingContent The optional content of the trailing icon.
 * @param colors The colors to be used for the list item. They are styled by default to match the Vxm theme.
 */
@Composable
fun VxmListItem(
    modifier: Modifier = Modifier,
    headlineContent: @Composable () -> Unit,
    supportingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    colors: ListItemColors = ListItemDefaults.colors(
        containerColor = MaterialTheme.colorScheme.surface,
        headlineColor = MaterialTheme.colorScheme.onSurface,
        supportingColor = MaterialTheme.colorScheme.onSurface,
        trailingIconColor = MaterialTheme.colorScheme.onSurface
    )
) {
    ListItem(
        modifier = modifier,
        headlineContent = headlineContent,
        supportingContent = supportingContent,
        trailingContent = trailingContent,
        colors = colors
    )
}

@ThemePreviews
@Composable
private fun PreviewVxmListItem() {
    VxmListItem(
        headlineContent = { Text(text = "Headline") },
        supportingContent = { Text(text = "Supporting") },
        trailingContent = { Text(text = "Trailing") }
    )
}