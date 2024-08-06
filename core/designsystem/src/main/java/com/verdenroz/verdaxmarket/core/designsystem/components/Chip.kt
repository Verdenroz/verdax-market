package com.verdenroz.verdaxmarket.core.designsystem.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.verdenroz.verdaxmarket.core.designsystem.theme.ThemePreviews
import com.verdenroz.verdaxmarket.core.designsystem.theme.VxmTheme

/**
 * VerdaxMarket wrapper around [FilterChip] to display a chip with a label and an optional trailing icon.
 * @param selected The state of the chip.
 * @param onClick The callback to be invoked when the chip is clicked.
 * @param label The label of the chip.
 * @param trailingIcon The optional trailing icon of the chip.
 */
@Composable
fun VxmFilterChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
            )
        },
        trailingIcon = trailingIcon,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.secondary,
            selectedLabelColor = MaterialTheme.colorScheme.onSecondary,
            selectedTrailingIconColor = MaterialTheme.colorScheme.onSecondary,
            containerColor = Color.Transparent,
            labelColor = MaterialTheme.colorScheme.onBackground,
            iconColor = MaterialTheme.colorScheme.onBackground,
        )
    )
}

@ThemePreviews
@Composable
private fun VxmFilterChipPreview() {
    VxmTheme {
        Surface {
            Row {
                VxmFilterChip(
                    selected = true,
                    onClick = {},
                    label = "Selected",
                    trailingIcon = null
                )
                VxmFilterChip(
                    selected = false,
                    onClick = {},
                    label = "Unselected",
                    trailingIcon = null
                )
            }
        }
    }
}