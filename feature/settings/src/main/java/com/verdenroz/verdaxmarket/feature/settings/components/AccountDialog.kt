package com.verdenroz.verdaxmarket.feature.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.verdenroz.verdaxmarket.core.designsystem.components.VxmAsyncImage
import com.verdenroz.verdaxmarket.core.designsystem.icons.VxmIcons
import com.verdenroz.verdaxmarket.core.designsystem.theme.ThemePreviews
import com.verdenroz.verdaxmarket.core.designsystem.theme.VxmTheme
import com.verdenroz.verdaxmarket.feature.settings.R
import com.verdenroz.verdaxmarket.feature.settings.UserAuthState

@Composable
internal fun AccountDialog(
    user: UserAuthState.SignedIn,
    onSignOut: () -> Unit,
    onDismiss: () -> Unit
) {
    var showSignOutDialog by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Icon(
                            imageVector = VxmIcons.Remove,
                            contentDescription = stringResource(R.string.feature_settings_close)
                        )
                    }
                }

                if (user.photoUrl.isBlank()) {
                    Icon(
                        imageVector = VxmIcons.Account,
                        contentDescription = stringResource(R.string.feature_settings_profile_picture),
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                    )
                } else {
                    VxmAsyncImage(
                        model = user.photoUrl,
                        description = stringResource(id = R.string.feature_settings_profile_picture),
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape),
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (user.displayName.isNotBlank()) {
                    Text(
                        text = user.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = user.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = stringResource(id = R.string.feature_settings_created_on) + user.creationDate,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { showSignOutDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.inverseSurface),
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.feature_settings_sign_out),
                        color = MaterialTheme.colorScheme.inverseOnSurface
                    )
                }
            }
        }
    }

    if (showSignOutDialog) {
        SignOutDialog(
            onDismiss = { showSignOutDialog = false },
            onSignOut = onSignOut
        )
    }
}

@ThemePreviews
@Composable
private fun PreviewAccountDialog() {
    VxmTheme {
        AccountDialog(
            user = UserAuthState.SignedIn(
                displayName = "John Doe",
                email = "jojndoe@gmail.com",
                photoUrl = "",
                creationDate = "November 10, 2024",
            ),
            onSignOut = {},
            onDismiss = {}
        )
    }
}