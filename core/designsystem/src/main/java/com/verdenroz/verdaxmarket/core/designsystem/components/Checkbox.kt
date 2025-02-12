package com.verdenroz.verdaxmarket.core.designsystem.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import com.verdenroz.verdaxmarket.core.designsystem.theme.ThemePreviews
import com.verdenroz.verdaxmarket.core.designsystem.theme.VxmTheme

/**
 * VerdaxMarket wrapper around [Checkbox] to display a checkbox with a label.
 * @param checked The state of the checkbox.
 * @param onCheckedChange The callback to be invoked when the state of the checkbox changes.
 * @param text The label of the checkbox.
 */
@Composable
fun VxmCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.secondary,
                checkmarkColor = MaterialTheme.colorScheme.onSecondary,
                uncheckedColor = MaterialTheme.colorScheme.onBackground
            )
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@ThemePreviews
@Composable
private fun VxmCheckboxPreview() {
    VxmTheme {
        Surface {
            Row {
                VxmCheckbox(
                    checked = true,
                    onCheckedChange = {},
                    text = "Checkbox"
                )
                VxmCheckbox(
                    checked = false,
                    onCheckedChange = {},
                    text = "Checkbox"
                )
            }
        }
    }
}