package com.verdenroz.verdaxmarket.core.designsystem.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import com.verdenroz.verdaxmarket.core.designsystem.theme.ThemePreviews
import com.verdenroz.verdaxmarket.core.designsystem.theme.VxmTheme

/**
 * VerdaxMarket wrapper around [RadioButton] to display a radio button with a label.
 * @param selected The state of the radio button.
 * @param onClick The callback to be invoked when the radio button is clicked.
 * @param label The label of the radio button.
 */
@Composable
fun VxmRadioButton(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = MaterialTheme.colorScheme.secondary,
                unselectedColor = MaterialTheme.colorScheme.onSurface
            )
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@ThemePreviews
@Composable
private fun VxmRadioButtonPreview() {
    VxmTheme {
        VxmRadioButton(
            selected = true,
            onClick = {},
            label = "Selected"
        )
    }
}