package com.verdenroz.verdaxmarket.feature.settings.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.verdenroz.verdaxmarket.core.designsystem.theme.ThemePreviews
import com.verdenroz.verdaxmarket.core.designsystem.theme.VxmTheme
import com.verdenroz.verdaxmarket.feature.settings.R


@Composable
internal fun SignOutDialog(
    onDismiss: () -> Unit,
    onSignOut: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.feature_settings_sign_out)) },
        text = { Text(text = stringResource(R.string.feature_settings_sign_out_confirmation)) },
        confirmButton = {
            TextButton(onClick = {
                onDismiss()
                onSignOut()
            }) {
                Text(
                    text = stringResource(R.string.feature_settings_confirm),
                    color = MaterialTheme.colorScheme.inverseSurface
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.feature_settings_cancel),
                    color = MaterialTheme.colorScheme.inverseSurface
                )
            }
        }
    )
}

@ThemePreviews
@Composable
private fun PreviewSignOutDialog() {
    VxmTheme {
        SignOutDialog(
            onDismiss = {},
            onSignOut = {}
        )
    }
}