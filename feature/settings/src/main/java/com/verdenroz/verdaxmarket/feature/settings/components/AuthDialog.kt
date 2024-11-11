package com.verdenroz.verdaxmarket.feature.settings.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.verdenroz.verdaxmarket.core.designsystem.icons.VxmIcons
import com.verdenroz.verdaxmarket.core.designsystem.theme.ThemePreviews
import com.verdenroz.verdaxmarket.core.designsystem.theme.VxmTheme
import com.verdenroz.verdaxmarket.core.designsystem.theme.authActionColor
import com.verdenroz.verdaxmarket.core.designsystem.theme.authFontFamily
import com.verdenroz.verdaxmarket.feature.settings.R


@Composable
internal fun AuthDialog(
    onSignUpWithEmail: (String, String) -> Unit,
    onSignInWithEmail: (String, String) -> Unit,
    onSignInWithGoogle: () -> Unit,
    onSignInWithGithub: () -> Unit,
    onForgetPassword: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var isSigningUp by remember { mutableStateOf(false) }
    var isSigningIn by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Action Buttons
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    // Back button - only show when signing in/up with email
                    if (isSigningIn || isSigningUp) {
                        IconButton(
                            onClick = {
                                isSigningIn = false
                                isSigningUp = false
                            },
                            modifier = Modifier.align(Alignment.CenterStart)
                        ) {
                            Icon(
                                imageVector = VxmIcons.ArrowBack,
                                contentDescription = stringResource(R.string.feature_settings_back)
                            )
                        }
                    }

                    //  Close button
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Icon(
                            imageVector = VxmIcons.Remove,
                            contentDescription = stringResource(R.string.feature_settings_close)
                        )
                    }
                }

                // Header text
                Text(
                    text = when {
                        isSigningIn -> stringResource(R.string.feature_settings_sign_in_with_email)
                        isSigningUp -> stringResource(R.string.feature_settings_create_account)
                        else -> stringResource(R.string.feature_settings_welcome)
                    },
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 18.sp,
                    fontFamily = authFontFamily,
                    fontWeight = FontWeight.Bold,
                )

                // Content Section
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (isSigningIn) {
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text(stringResource(R.string.feature_settings_email)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedLabelColor = MaterialTheme.colorScheme.inverseSurface,
                                focusedBorderColor = MaterialTheme.colorScheme.inverseSurface,
                                focusedTextColor = MaterialTheme.colorScheme.inverseSurface,
                                cursorColor = MaterialTheme.colorScheme.inverseSurface,
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text(stringResource(R.string.feature_settings_password)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedLabelColor = MaterialTheme.colorScheme.inverseSurface,
                                focusedBorderColor = MaterialTheme.colorScheme.inverseSurface,
                                focusedTextColor = MaterialTheme.colorScheme.inverseSurface,
                                cursorColor = MaterialTheme.colorScheme.inverseSurface,
                            ),
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                        )
                        AuthButton(
                            text = stringResource(R.string.feature_settings_sign_in),
                            onClick = {
                                onSignInWithEmail(email, password)
                                onDismiss()
                            },
                        )
                        TextButton(onClick = { TODO("Forgot passwordf flow") }) {
                            Text(
                                text = stringResource(R.string.feature_settings_forgot_password),
                                color = authActionColor,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }

                    } else if (isSigningUp) {
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text(stringResource(R.string.feature_settings_email)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedLabelColor = MaterialTheme.colorScheme.inverseSurface,
                                focusedBorderColor = MaterialTheme.colorScheme.inverseSurface,
                                focusedTextColor = MaterialTheme.colorScheme.inverseSurface,
                                cursorColor = MaterialTheme.colorScheme.inverseSurface,
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text(stringResource(R.string.feature_settings_password)) },
                            visualTransformation = PasswordVisualTransformation(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedLabelColor = MaterialTheme.colorScheme.inverseSurface,
                                focusedBorderColor = MaterialTheme.colorScheme.inverseSurface,
                                focusedTextColor = MaterialTheme.colorScheme.inverseSurface,
                                cursorColor = MaterialTheme.colorScheme.inverseSurface,
                            ),
                            modifier = Modifier.fillMaxWidth(),
                        )
                        AuthButton(
                            text = stringResource(R.string.feature_settings_sign_up),
                            onClick = {
                                onSignUpWithEmail(email, password)
                                isSigningUp = false
                                onDismiss()
                            },
                            contentDescription = stringResource(id = R.string.feature_settings_sign_up),
                            modifier = Modifier.padding(horizontal = 32.dp, vertical = 16.dp)
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.feature_settings_continue_with),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        AuthButton(
                            logo = painterResource(id = R.drawable.feature_settings_google_logo),
                            contentDescription = stringResource(id = R.string.feature_settings_sign_in_with_google),
                            text = stringResource(id = R.string.feature_settings_sign_in_with_google),
                            onClick = {
                                onSignInWithGoogle()
                                onDismiss()
                            }
                        )
                        AuthButton(
                            logo = painterResource(id = R.drawable.feature_settings_github_logo),
                            contentDescription = stringResource(id = R.string.feature_settings_sign_in_with_github),
                            text = stringResource(id = R.string.feature_settings_sign_in_with_github),
                            onClick = {
                                onSignInWithGithub()
                                onDismiss()
                            }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        AuthButton(
                            imageVector = VxmIcons.Email,
                            contentDescription = stringResource(id = R.string.feature_settings_sign_in_with_email),
                            onClick = { isSigningIn = true }
                        )


                        HorizontalDivider()

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.feature_settings_sign_up_prompt),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            TextButton(
                                onClick = { isSigningUp = true }
                            ) {
                                Text(
                                    text = stringResource(R.string.feature_settings_sign_up),
                                    color = MaterialTheme.colorScheme.inverseSurface,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@ThemePreviews
@Composable
private fun PreviewAuthDialog() {
    VxmTheme {
        AuthDialog(
            onSignInWithEmail = { _, _ -> },
            onSignUpWithEmail = { _, _ -> },
            onForgetPassword = {},
            onSignInWithGoogle = {},
            onSignInWithGithub = {},
            onDismiss = {},
        )
    }
}

@Composable
private fun AuthButton(
    modifier: Modifier = Modifier,
    logo: Painter? = null,
    imageVector: ImageVector? = null,
    contentDescription: String? = null,
    text: String? = null,
    onClick: () -> Unit,
) {
    VxmTheme { isDarkTheme ->
        val backgroundColor = if (isDarkTheme) Color(0xFF131314) else Color(0xFFFFFFFF)
        val strokeColor = if (isDarkTheme) Color(0xFF8E918F) else Color(0xFF747775)
        val textColor = if (isDarkTheme) Color(0xFFE3E3E3) else Color(0xFF1F1F1F)

        Row(
            horizontalArrangement = when {
                logo != null || imageVector != null -> Arrangement.spacedBy(8.dp)
                else -> Arrangement.Center
            },
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .height(44.dp)
                .let {
                    if (logo != null || imageVector != null) it else it.fillMaxWidth()
                }
                .clip(CircleShape)
                .background(backgroundColor)
                .border(1.dp, strokeColor, CircleShape)
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp)
        ) {
            if (logo != null || imageVector != null) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                ) {
                    when {
                        logo != null -> {
                            Image(
                                painter = logo,
                                contentDescription = contentDescription,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        imageVector != null -> {
                            Icon(
                                imageVector = imageVector,
                                contentDescription = contentDescription,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
            if (text != null) {
                Text(
                    text = text,
                    fontFamily = authFontFamily,
                    color = textColor,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }
        }
    }
}