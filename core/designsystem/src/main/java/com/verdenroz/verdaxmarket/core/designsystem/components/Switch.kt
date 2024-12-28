package com.verdenroz.verdaxmarket.core.designsystem.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.verdenroz.verdaxmarket.core.designsystem.theme.ThemePreviews
import com.verdenroz.verdaxmarket.core.designsystem.theme.VxmTheme

/**
 * VerdaxMarket wrapper around [Switch] to display a checkbox with a label.
 * @param checked The state of the checkbox.
 * @param onCheckedChange The callback to be invoked when the state of the checkbox changes.
 */
@Composable
fun VxmSwitch(
    modifier: Modifier = Modifier,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        colors = SwitchDefaults.colors(
            checkedThumbColor = MaterialTheme.colorScheme.inverseOnSurface,
            uncheckedThumbColor = MaterialTheme.colorScheme.inverseOnSurface,
            checkedTrackColor = MaterialTheme.colorScheme.inverseSurface,
            uncheckedTrackColor = MaterialTheme.colorScheme.inverseSurface
        ),
        modifier = modifier
    )
}

@ThemePreviews
@Composable
private fun PreviewVxmSwitch() {
    VxmTheme {
        Column {
            VxmSwitch(checked = true, onCheckedChange = {})
            VxmSwitch(checked = false, onCheckedChange = {})
        }
    }
}