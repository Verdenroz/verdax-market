package com.verdenroz.verdaxmarket.feature.settings.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.google.firebase.auth.EmailAuthProvider
import com.verdenroz.verdaxmarket.core.designsystem.components.VxmAsyncImage
import com.verdenroz.verdaxmarket.core.designsystem.components.VxmSwitch
import com.verdenroz.verdaxmarket.core.designsystem.icons.VxmIcons
import com.verdenroz.verdaxmarket.core.designsystem.theme.ThemePreviews
import com.verdenroz.verdaxmarket.core.designsystem.theme.VxmTheme
import com.verdenroz.verdaxmarket.core.designsystem.util.isLandscape
import com.verdenroz.verdaxmarket.feature.settings.R
import com.verdenroz.verdaxmarket.feature.settings.UserAuthState

@Composable
internal fun AccountDialog(
    user: UserAuthState.SignedIn,
    isSynced: Boolean,
    onDismiss: () -> Unit,
    onSignOut: () -> Unit,
    onDeleteAccount: (String?) -> Unit,
    onSyncChange: (Boolean) -> Unit
) {
    var showSignOutDialog by rememberSaveable { mutableStateOf(false) }
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    var showEmailReauthDialog by rememberSaveable { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .heightIn(max = 475.dp)
        ) {
            Column {
                // Header with close button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Icon(
                            imageVector = VxmIcons.Close,
                            contentDescription = stringResource(R.string.feature_settings_close)
                        )
                    }
                }

                // Main content
                if (isLandscape()) {
                    AccountDialogLandscapeContent(
                        user = user,
                        isSynced = isSynced,
                        onDeleteAccount = { showDeleteDialog = true },
                        onSyncChange = onSyncChange
                    )
                } else {
                    AccountDialogPortraitContent(
                        user = user,
                        isSynced = isSynced,
                        onDeleteAccount = { showDeleteDialog = true },
                        onSyncChange = onSyncChange
                    )
                }

                // Sign out button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = { showSignOutDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.inverseSurface
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.feature_settings_sign_out),
                            color = MaterialTheme.colorScheme.inverseOnSurface
                        )
                    }
                }
            }
        }
    }

    // Dialogs
    if (showSignOutDialog) {
        SignOutDialog(
            onDismiss = { showSignOutDialog = false },
            onSignOut = onSignOut
        )
    }

    if (showDeleteDialog) {
        DeleteAccountDialog(
            onDismiss = { showDeleteDialog = false },
            onConfirmDelete = {
                val fiveMinutesInMillis = 5 * 60 * 1000
                val currentTime = System.currentTimeMillis()

                // Show reauth dialog only if it has been more than five minutes since last sign in
                if (user.providerId == EmailAuthProvider.PROVIDER_ID && (currentTime - user.lastSignIn > fiveMinutesInMillis)) {
                    showEmailReauthDialog = true
                } else {
                    onDeleteAccount(null)
                }
            }
        )
    }

    if (showEmailReauthDialog) {
        EmailReauthDialog(
            onDismiss = { showEmailReauthDialog = false },
            onConfirm = { password ->
                onDeleteAccount(password)
                showEmailReauthDialog = false
            }
        )
    }
}

@Composable
private fun AccountDialogLandscapeContent(
    user: UserAuthState.SignedIn,
    isSynced: Boolean,
    onDeleteAccount: () -> Unit,
    onSyncChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .heightIn(max = 200.dp)
            .wrapContentSize(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        UserProfileLandscape(
            user = user,
            onDeleteAccount = onDeleteAccount,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(vertical = 4.dp, horizontal = 8.dp)
        )

        VerticalDivider()

        SyncSectionLandscape(
            isSynced = isSynced,
            onSyncChange = onSyncChange,
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 4.dp, horizontal = 8.dp)
        )
    }
}

@Composable
private fun AccountDialogPortraitContent(
    user: UserAuthState.SignedIn,
    isSynced: Boolean,
    onDeleteAccount: () -> Unit,
    onSyncChange: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        UserProfilePortrait(
            user = user,
            onDeleteAccount = onDeleteAccount,
        )

        Spacer(modifier = Modifier.height(16.dp))

        SyncSectionPortrait(
            isSynced = isSynced,
            onSyncChange = onSyncChange,
        )
    }
}

@Composable
private fun UserProfileLandscape(
    user: UserAuthState.SignedIn,
    onDeleteAccount: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (user.photoUrl.isBlank()) {
                Icon(
                    imageVector = VxmIcons.Account,
                    contentDescription = stringResource(R.string.feature_settings_profile_picture),
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                )
            } else {
                VxmAsyncImage(
                    model = user.photoUrl,
                    description = stringResource(id = R.string.feature_settings_profile_picture),
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape),
                )
            }

            if (user.displayName.isNotBlank()) {
                Text(
                    text = user.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            if (user.email.isNotBlank()) {
                Text(
                    text = user.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Text(
                text = stringResource(id = R.string.feature_settings_created_on) + user.creationDate,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        DeleteAccountButton(onDeleteAccount = onDeleteAccount)
    }
}

@Composable
private fun UserProfilePortrait(
    user: UserAuthState.SignedIn,
    onDeleteAccount: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
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

        if (user.displayName.isNotBlank()) {
            Text(
                text = user.displayName,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        if (user.email.isNotBlank()) {
            Text(
                text = user.email,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Text(
            text = stringResource(id = R.string.feature_settings_created_on) + user.creationDate,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        DeleteAccountButton(onDeleteAccount = onDeleteAccount)
    }
}

@Composable
private fun DeleteAccountButton(
    onDeleteAccount: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable(onClick = onDeleteAccount)
            .padding(8.dp)
    ) {
        Icon(
            imageVector = VxmIcons.Delete,
            contentDescription = stringResource(R.string.feature_settings_delete_account),
            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.75f),
            modifier = Modifier.size(16.dp)
        )

        Text(
            text = stringResource(R.string.feature_settings_delete_account),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error.copy(alpha = 0.75f)
        )
    }
}

@Composable
private fun SyncSectionLandscape(
    isSynced: Boolean,
    onSyncChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = VxmIcons.Sync,
            contentDescription = stringResource(R.string.feature_settings_sync),
            modifier = Modifier.size(48.dp)
        )

        Text(
            text = stringResource(R.string.feature_settings_sync),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = stringResource(R.string.feature_settings_sync_description),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface
        )

        VxmSwitch(
            checked = isSynced,
            onCheckedChange = onSyncChange,
            modifier = Modifier.padding(bottom = 8.dp, top = 16.dp)
        )
    }
}

@Composable
private fun SyncSectionPortrait(
    isSynced: Boolean,
    onSyncChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = VxmIcons.Sync,
            contentDescription = stringResource(R.string.feature_settings_sync),
            modifier = Modifier.padding(start = 8.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = stringResource(R.string.feature_settings_sync),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = stringResource(R.string.feature_settings_sync_description),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        VxmSwitch(
            checked = isSynced,
            onCheckedChange = onSyncChange,
            modifier = Modifier.padding(end = 8.dp)
        )
    }
}

@Composable
private fun DeleteAccountDialog(
    onDismiss: () -> Unit,
    onConfirmDelete: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.feature_settings_delete_account_title)) },
        text = { Text(text = stringResource(R.string.feature_settings_delete_account_message)) },
        confirmButton = {
            TextButton(onClick = {
                onDismiss()
                onConfirmDelete()
            }) {
                Text(
                    text = stringResource(R.string.feature_settings_delete),
                    color = MaterialTheme.colorScheme.error
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

@Composable
private fun EmailReauthDialog(
    onDismiss: () -> Unit,
    onConfirm: (password: String) -> Unit
) {
    var password by rememberSaveable { mutableStateOf("") }
    var isPasswordVisible by rememberSaveable { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.feature_settings_confirm_password)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = stringResource(R.string.feature_settings_reauth_message))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text(stringResource(R.string.feature_settings_password)) },
                    singleLine = true,
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(
                                imageVector = if (isPasswordVisible) VxmIcons.VisibilityOff else VxmIcons.Visibility,
                                contentDescription = stringResource(R.string.feature_settings_toggle_visibility)
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedLabelColor = MaterialTheme.colorScheme.inverseSurface,
                        focusedBorderColor = MaterialTheme.colorScheme.inverseSurface,
                        focusedTextColor = MaterialTheme.colorScheme.inverseSurface,
                        cursorColor = MaterialTheme.colorScheme.inverseSurface,
                        errorBorderColor = MaterialTheme.colorScheme.error,
                        errorLabelColor = MaterialTheme.colorScheme.error,
                        errorSupportingTextColor = MaterialTheme.colorScheme.error,
                    ),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(password)
                    onDismiss()
                },
                enabled = password.isNotBlank()
            ) {
                Text(
                    text = stringResource(R.string.feature_settings_confirm),
                    color = MaterialTheme.colorScheme.error
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
private fun PreviewAccountDialog() {
    VxmTheme {
        AccountDialog(
            user = UserAuthState.SignedIn(
                displayName = "John Doe",
                email = "jojndoe@gmail.com",
                photoUrl = "",
                creationDate = "November 10, 2024",
                providerId = "google.com",
                lastSignIn = System.currentTimeMillis()
            ),
            isSynced = true,
            onDismiss = {},
            onSignOut = {},
            onDeleteAccount = {},
            onSyncChange = {}
        )
    }
}