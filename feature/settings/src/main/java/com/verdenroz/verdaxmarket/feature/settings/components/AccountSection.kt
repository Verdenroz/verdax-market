package com.verdenroz.verdaxmarket.feature.settings.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    onSignUpWithEmail: (String, String) -> Unit,
    onSignInWithEmail: (String, String) -> Unit,
    onSignInWithGoogle: () -> Unit,
    onSignInWithGithub: () -> Unit,
    onForgetPassword: (String) -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showAuthDialog by remember { mutableStateOf(false) }
    var showSignOutDialog by remember { mutableStateOf(false) }

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
                    Text(
                        text = userState.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                supportingContent = {
                    Text(
                        text = stringResource(id = R.string.feature_settings_created_on) + userState.creationDate,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                trailingContent = {
                    Icon(
                        imageVector = VxmIcons.Logout,
                        contentDescription = stringResource(R.string.feature_settings_sign_out)
                    )
                },
                modifier = modifier.clickable { onSignOut() }
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
    if (showAuthDialog) {
        AuthDialog(
            onSignInWithEmail = onSignInWithEmail,
            onSignUpWithEmail = onSignUpWithEmail,
            onSignInWithGoogle = onSignInWithGoogle,
            onSignInWithGithub = onSignInWithGithub,
            onForgetPassword = onForgetPassword,
            onDismiss = { showAuthDialog = false },
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
                    creationDate = "November 9, 2024"
                ),
                onSignUpWithEmail = { _, _ -> },
                onSignInWithEmail = { _, _ -> },
                onSignInWithGoogle = {},
                onSignInWithGithub = {},
                onForgetPassword = {},
                onSignOut = {}
            )
            AccountSection(
                userState = UserAuthState.SignedOut,
                onSignUpWithEmail = { _, _ -> },
                onSignInWithEmail = { _, _ -> },
                onSignInWithGoogle = {},
                onSignInWithGithub = {},
                onForgetPassword = {},
                onSignOut = {}
            )
            AccountSection(
                userState = UserAuthState.Loading,
                onSignUpWithEmail = { _, _ -> },
                onSignInWithEmail = { _, _ -> },
                onSignInWithGoogle = {},
                onSignInWithGithub = {},
                onForgetPassword = {},
                onSignOut = {}
            )
        }
    }
}