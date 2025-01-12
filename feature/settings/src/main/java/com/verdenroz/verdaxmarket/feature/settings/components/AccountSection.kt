package com.verdenroz.verdaxmarket.feature.settings.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.verdenroz.verdaxmarket.core.designsystem.components.VxmAsyncImage
import com.verdenroz.verdaxmarket.core.designsystem.components.VxmListItem
import com.verdenroz.verdaxmarket.core.designsystem.icons.VxmIcons
import com.verdenroz.verdaxmarket.core.designsystem.theme.ThemePreviews
import com.verdenroz.verdaxmarket.core.designsystem.theme.VxmTheme
import com.verdenroz.verdaxmarket.feature.settings.R
import com.verdenroz.verdaxmarket.feature.settings.UserAuthState

@Composable
fun AccountSection(
    userState: UserAuthState,
    isSynced: Boolean,
    onSignUpWithEmail: (String, String) -> Unit,
    onSignInWithEmail: (String, String) -> Unit,
    onSignInWithGoogle: () -> Unit,
    onSignInWithGithub: () -> Unit,
    onForgetPassword: (String) -> Unit,
    onSignOut: () -> Unit,
    onDeleteAccount: (String?) -> Unit,
    onSyncChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAccountDialog by rememberSaveable { mutableStateOf(false) }
    var showAuthDialog by rememberSaveable { mutableStateOf(false) }
    var showSignOutDialog by rememberSaveable { mutableStateOf(false) }

    when (userState) {
        is UserAuthState.SignedIn -> {
            VxmListItem(
                leadingContent = {
                    if (userState.photoUrl.isBlank()) {
                        Icon(
                            imageVector = VxmIcons.Account,
                            contentDescription = stringResource(R.string.feature_settings_profile_picture),
                            modifier = Modifier.size(48.dp)
                        )
                    } else {
                        VxmAsyncImage(
                            model = userState.photoUrl,
                            description = stringResource(id = R.string.feature_settings_profile_picture),
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape),
                        )
                    }
                },
                overlineContent = {
                    Text(
                        text = userState.email,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                headlineContent = {
                    if (userState.displayName.isNotBlank()) {
                        Text(
                            text = userState.displayName,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    } else
                        Text(
                            text = stringResource(id = R.string.feature_settings_created_on) + userState.creationDate,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                },
                trailingContent = {
                    IconButton(
                        onClick = { showSignOutDialog = true },
                        modifier = Modifier.pointerInput(Unit) {
                            awaitPointerEventScope {
                                awaitFirstDown(false)
                                // Consume the event to prevent parent click
                            }
                        }
                    ) {
                        Icon(
                            imageVector = VxmIcons.Logout,
                            contentDescription = stringResource(R.string.feature_settings_sign_out)
                        )
                    }
                },
                modifier = modifier.pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { showAccountDialog = true }
                    )
                }
            )
        }

        UserAuthState.SignedOut -> {
            VxmListItem(
                leadingContent = {
                    Icon(
                        imageVector = VxmIcons.Account,
                        contentDescription = stringResource(R.string.feature_settings_sign_in),
                        modifier = Modifier.size(48.dp)
                    )
                },
                headlineContent = {
                    Text(
                        text = stringResource(id = R.string.feature_settings_sign_in),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                trailingContent = {
                    Icon(
                        imageVector = VxmIcons.Login,
                        contentDescription = stringResource(R.string.feature_settings_sign_in)
                    )
                },
                modifier = modifier.clickable { showAuthDialog = true }
            )
        }

        UserAuthState.Loading -> {
            VxmListItem(
                leadingContent = {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                headlineContent = {
                    Text(
                        text = stringResource(id = R.string.feature_settings_sign_in),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                trailingContent = {
                    Icon(
                        imageVector = VxmIcons.Login,
                        contentDescription = stringResource(R.string.feature_settings_sign_in)
                    )
                }
            )
        }
    }

    if (showAccountDialog && userState is UserAuthState.SignedIn) {
        AccountDialog(
            user = userState,
            isSynced = isSynced,
            onDismiss = { showAccountDialog = false },
            onSignOut = onSignOut,
            onDeleteAccount = onDeleteAccount,
            onSyncChange = onSyncChange
        )
    }

    if (showAuthDialog) {
        AuthDialog(
            onSignInWithEmail = onSignInWithEmail,
            onSignUpWithEmail = onSignUpWithEmail,
            onSignInWithGoogle = onSignInWithGoogle,
            onSignInWithGithub = onSignInWithGithub,
            onForgetPassword = onForgetPassword,
            onDismiss = { showAuthDialog = false },
            onSuccess = {
                showAuthDialog = false
                showAccountDialog = true
            }
        )
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
fun PreviewAccountSection() {
    VxmTheme {
        Column {
            AccountSection(
                userState = UserAuthState.SignedIn(
                    displayName = "John Doe",
                    email = "johndoe@gmail.com",
                    photoUrl = "",
                    creationDate = "November 9, 2024",
                    providerId = "google.com"
                ),
                isSynced = true,
                onSignUpWithEmail = { _, _ -> },
                onSignInWithEmail = { _, _ -> },
                onSignInWithGoogle = {},
                onSignInWithGithub = {},
                onForgetPassword = {},
                onSignOut = {},
                onDeleteAccount = {},
                onSyncChange = {}
            )
            AccountSection(
                userState = UserAuthState.SignedOut,
                isSynced = true,
                onSignUpWithEmail = { _, _ -> },
                onSignInWithEmail = { _, _ -> },
                onSignInWithGoogle = {},
                onSignInWithGithub = {},
                onForgetPassword = {},
                onSignOut = {},
                onDeleteAccount = {},
                onSyncChange = {}
            )
            AccountSection(
                userState = UserAuthState.Loading,
                isSynced = true,
                onSignUpWithEmail = { _, _ -> },
                onSignInWithEmail = { _, _ -> },
                onSignInWithGoogle = {},
                onSignInWithGithub = {},
                onForgetPassword = {},
                onSignOut = {},
                onDeleteAccount = {},
                onSyncChange = {}
            )
        }
    }
}