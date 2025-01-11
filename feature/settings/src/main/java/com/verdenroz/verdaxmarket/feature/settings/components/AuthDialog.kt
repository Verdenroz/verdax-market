package com.verdenroz.verdaxmarket.feature.settings.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.verdenroz.verdaxmarket.core.common.error.DataError
import com.verdenroz.verdaxmarket.core.designsystem.components.VxmBackIconButton
import com.verdenroz.verdaxmarket.core.designsystem.icons.VxmIcons
import com.verdenroz.verdaxmarket.core.designsystem.theme.LocalTheme
import com.verdenroz.verdaxmarket.core.designsystem.theme.ThemePreviews
import com.verdenroz.verdaxmarket.core.designsystem.theme.VxmTheme
import com.verdenroz.verdaxmarket.core.designsystem.theme.authActionColor
import com.verdenroz.verdaxmarket.core.designsystem.theme.authFontFamily
import com.verdenroz.verdaxmarket.core.designsystem.util.asUiText
import com.verdenroz.verdaxmarket.feature.settings.R

private data class ValidationState(
    val isValid: Boolean,
    val error: DataError.Local? = null
)

private fun validateEmail(email: String): ValidationState {
    return when {
        email.isBlank() -> ValidationState(
            isValid = false,
            error = DataError.Local.BLANK_EMAIL
        )

        !email.matches(Regex("[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")) -> ValidationState(
            isValid = false,
            error = DataError.Local.INVALID_EMAIL
        )

        else -> ValidationState(isValid = true)
    }
}

private fun validatePassword(password: String, confirmPassword: String): ValidationState {
    return when {
        password.isBlank() -> ValidationState(
            isValid = false,
            error = DataError.Local.BLANK_PASSWORD
        )

        password.length < 6 -> ValidationState(
            isValid = false,
            error = DataError.Local.INVALID_PASSWORD
        )

        confirmPassword != password -> ValidationState(
            isValid = false,
            error = DataError.Local.CONFIRM_PASSWORD_MISMATCH
        )

        else -> ValidationState(isValid = true)
    }
}

private val DataErrorLocalSaver = Saver<DataError.Local?, String>(
    save = { error ->
        when (error) {
            DataError.Local.BLANK_EMAIL -> "BLANK_EMAIL"
            DataError.Local.INVALID_EMAIL -> "INVALID_EMAIL"
            DataError.Local.BLANK_PASSWORD -> "BLANK_PASSWORD"
            DataError.Local.INVALID_PASSWORD -> "INVALID_PASSWORD"
            DataError.Local.CONFIRM_PASSWORD_MISMATCH -> "CONFIRM_PASSWORD_MISMATCH"
            null -> "null"
        }
    },
    restore = { value ->
        when (value) {
            "BLANK_EMAIL" -> DataError.Local.BLANK_EMAIL
            "INVALID_EMAIL" -> DataError.Local.INVALID_EMAIL
            "BLANK_PASSWORD" -> DataError.Local.BLANK_PASSWORD
            "INVALID_PASSWORD" -> DataError.Local.INVALID_PASSWORD
            "CONFIRM_PASSWORD_MISMATCH" -> DataError.Local.CONFIRM_PASSWORD_MISMATCH
            else -> null
        }
    }
)


@Composable
internal fun AuthDialog(
    onSignUpWithEmail: (String, String) -> Unit,
    onSignInWithEmail: (String, String) -> Unit,
    onSignInWithGoogle: () -> Unit,
    onSignInWithGithub: () -> Unit,
    onForgetPassword: (String) -> Unit,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    var isSigningUp by rememberSaveable { mutableStateOf(false) }
    var isSigningIn by rememberSaveable { mutableStateOf(false) }
    var isForgotPassword by rememberSaveable { mutableStateOf(false) }
    var isPasswordResetEmailSent by rememberSaveable { mutableStateOf(false) }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var confirmPasswordVisible by rememberSaveable { mutableStateOf(false) }

    var emailError by rememberSaveable(stateSaver = DataErrorLocalSaver) {
        mutableStateOf<DataError.Local?>(null)
    }
    var passwordError by rememberSaveable(stateSaver = DataErrorLocalSaver) {
        mutableStateOf<DataError.Local?>(null)
    }
    var confirmPasswordError by rememberSaveable(stateSaver = DataErrorLocalSaver) {
        mutableStateOf<DataError.Local?>(null)
    }

    val passwordFocusRequester = remember { FocusRequester() }
    val confirmPasswordFocusRequester = remember { FocusRequester() }

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
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Back button - only show when signing in/up with email or forgot password
                    if (isSigningIn || isSigningUp || isForgotPassword) {
                        VxmBackIconButton(
                            onClick = {
                                isSigningIn = false
                                isSigningUp = false
                                isForgotPassword = false
                                isPasswordResetEmailSent = false
                            },
                            modifier = Modifier.align(Alignment.CenterStart)
                        )
                    }

                    //  Close button
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Icon(
                            imageVector = VxmIcons.Close,
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
                    when {
                        isForgotPassword -> {
                            if (!isPasswordResetEmailSent) {
                                Text(
                                    text = stringResource(R.string.feature_settings_forgot_password_description),
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                AuthOutlinedTextField(
                                    value = email,
                                    onValueChange = {
                                        email = it
                                        emailError = null
                                    },
                                    label = stringResource(R.string.feature_settings_email),
                                    keyBoardOptions = KeyboardOptions.Default.copy(
                                        keyboardType = KeyboardType.Email,
                                        imeAction = ImeAction.Done
                                    ),
                                    keyBoardAction = KeyboardActions(
                                        onDone = {
                                            val validation = validateEmail(email)
                                            if (validation.isValid) {
                                                onForgetPassword(email)
                                                isPasswordResetEmailSent = true
                                            } else {
                                                emailError = validation.error
                                            }
                                        }
                                    ),
                                    isError = emailError != null,
                                    errorText = emailError?.asUiText()?.asString()
                                )
                                AuthButton(
                                    text = stringResource(R.string.feature_settings_send_reset_link),
                                    onClick = {
                                        val validation = validateEmail(email)
                                        if (validation.isValid) {
                                            onForgetPassword(email)
                                            isPasswordResetEmailSent = true
                                        } else {
                                            emailError = validation.error
                                        }
                                    },
                                    enabled = email.isNotBlank()
                                )
                            } else {
                                Icon(
                                    imageVector = VxmIcons.CheckCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(48.dp)
                                )
                                Text(
                                    text = stringResource(
                                        R.string.feature_settings_reset_link_sent,
                                        email
                                    ),
                                    style = MaterialTheme.typography.bodyLarge,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = stringResource(R.string.feature_settings_check_email_instructions),
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                AuthButton(
                                    text = stringResource(R.string.feature_settings_back_to_sign_in),
                                    onClick = {
                                        isForgotPassword = false
                                        isPasswordResetEmailSent = false
                                        isSigningIn = true
                                    }
                                )
                            }
                        }

                        isSigningIn -> {
                            AuthOutlinedTextField(
                                value = email,
                                onValueChange = {
                                    email = it
                                    emailError = null
                                },
                                label = stringResource(R.string.feature_settings_email),
                                keyBoardOptions = KeyboardOptions.Default.copy(
                                    keyboardType = KeyboardType.Email,
                                    imeAction = ImeAction.Next
                                ),
                                keyBoardAction = KeyboardActions(
                                    onNext = {
                                        passwordFocusRequester.requestFocus()
                                    }
                                ),
                                isError = emailError != null,
                                errorText = emailError?.asUiText()?.asString()
                            )

                            AuthOutlinedTextField(
                                value = password,
                                onValueChange = {
                                    password = it
                                    passwordError = null
                                },
                                label = stringResource(R.string.feature_settings_password),
                                keyBoardOptions = KeyboardOptions.Default.copy(
                                    keyboardType = KeyboardType.Password,
                                    imeAction = ImeAction.Done
                                ),
                                keyBoardAction = KeyboardActions(
                                    onDone = {
                                        onSignInWithEmail(email, password)
                                        onSuccess()
                                    }
                                ),
                                isError = passwordError != null,
                                errorText = passwordError?.asUiText()?.asString(),
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                trailingIcon = {
                                    val image =
                                        if (passwordVisible) VxmIcons.Visibility else VxmIcons.VisibilityOff

                                    IconButton(onClick = {
                                        passwordVisible = !passwordVisible
                                    }) {
                                        Icon(imageVector = image, contentDescription = stringResource(R.string.feature_settings_toggle_visibility))
                                    }
                                },
                                modifier = Modifier.focusRequester(passwordFocusRequester)
                            )
                            AuthButton(
                                text = stringResource(R.string.feature_settings_sign_in),
                                onClick = { onSignInWithEmail(email, password) },
                                enabled = email.isNotBlank() && password.isNotBlank()
                            )
                            TextButton(onClick = { isForgotPassword = true }) {
                                Text(
                                    text = stringResource(R.string.feature_settings_forgot_password),
                                    color = authActionColor,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }

                        isSigningUp -> {
                            AuthOutlinedTextField(
                                value = email,
                                onValueChange = {
                                    email = it
                                    emailError = null
                                },
                                label = stringResource(R.string.feature_settings_email),
                                keyBoardOptions = KeyboardOptions.Default.copy(
                                    keyboardType = KeyboardType.Email,
                                    imeAction = ImeAction.Next
                                ),
                                keyBoardAction = KeyboardActions(
                                    onNext = {
                                        passwordFocusRequester.requestFocus()
                                    }
                                ),
                                isError = emailError != null,
                                errorText = emailError?.asUiText()?.asString()
                            )

                            AuthOutlinedTextField(
                                value = password,
                                onValueChange = {
                                    password = it
                                    passwordError = null
                                },
                                label = stringResource(R.string.feature_settings_password),
                                keyBoardOptions = KeyboardOptions.Default.copy(
                                    keyboardType = KeyboardType.Password,
                                    imeAction = ImeAction.Next
                                ),
                                keyBoardAction = KeyboardActions(
                                    onNext = {
                                        confirmPasswordFocusRequester.requestFocus()
                                    }
                                ),
                                isError = passwordError != null,
                                errorText = passwordError?.asUiText()?.asString(),
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                trailingIcon = {
                                    val image = if (passwordVisible)
                                        VxmIcons.Visibility
                                    else VxmIcons.VisibilityOff

                                    IconButton(onClick = {
                                        passwordVisible = !passwordVisible
                                    }) {
                                        Icon(
                                            imageVector = image,
                                            contentDescription = stringResource(R.string.feature_settings_toggle_visibility)
                                        )
                                    }
                                },
                                modifier = Modifier.focusRequester(passwordFocusRequester)
                            )

                            AuthOutlinedTextField(
                                value = confirmPassword,
                                onValueChange = {
                                    confirmPassword = it
                                    confirmPasswordError = null
                                },
                                label = stringResource(R.string.feature_settings_confirm_password),
                                keyBoardOptions = KeyboardOptions.Default.copy(
                                    keyboardType = KeyboardType.Password,
                                    imeAction = ImeAction.Done
                                ),
                                keyBoardAction = KeyboardActions(
                                    onDone = {
                                        val emailValidation = validateEmail(email)
                                        val passwordValidation =
                                            validatePassword(password, confirmPassword)

                                        if (emailValidation.isValid && passwordValidation.isValid) {
                                            onSignUpWithEmail(email, password)
                                            onSuccess()
                                        } else {
                                            emailError = emailValidation.error
                                            passwordError = passwordValidation.error
                                            confirmPasswordError =
                                                if (confirmPassword != password) DataError.Local.INVALID_PASSWORD else null
                                        }
                                    }
                                ),
                                isError = confirmPasswordError != null,
                                errorText = confirmPasswordError?.asUiText()?.asString(),
                                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                trailingIcon = {
                                    val image = if (confirmPasswordVisible)
                                        VxmIcons.Visibility
                                    else VxmIcons.VisibilityOff

                                    IconButton(onClick = {
                                        confirmPasswordVisible = !confirmPasswordVisible
                                    }) {
                                        Icon(
                                            imageVector = image,
                                            contentDescription = stringResource(R.string.feature_settings_toggle_visibility)
                                        )
                                    }
                                },
                                modifier = Modifier.focusRequester(confirmPasswordFocusRequester)
                            )

                            AuthButton(
                                text = stringResource(R.string.feature_settings_sign_up),
                                onClick = {
                                    val emailValidation = validateEmail(email)
                                    val passwordValidation = validatePassword(password, confirmPassword)

                                    if (emailValidation.isValid && passwordValidation.isValid) {
                                        onSignUpWithEmail(email, password)
                                        onSuccess()
                                    } else {
                                        emailError = emailValidation.error
                                        passwordError = passwordValidation.error
                                        confirmPasswordError = passwordValidation.error
                                    }
                                },
                                contentDescription = stringResource(id = R.string.feature_settings_sign_up),
                                modifier = Modifier.padding(horizontal = 32.dp, vertical = 16.dp),
                                enabled = email.isNotBlank() && password.isNotBlank() && confirmPassword.isNotBlank()
                            )
                        }

                        else -> {
                            Text(
                                text = stringResource(R.string.feature_settings_continue_with),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            AuthButton(
                                logo = painterResource(id = if (LocalTheme.current) R.drawable.feature_settings_google_logo_dark else R.drawable.feature_settings_google_logo_light),
                                contentDescription = stringResource(id = R.string.feature_settings_sign_in_with_google),
                                text = stringResource(id = R.string.feature_settings_sign_in_with_google),
                                onClick = {
                                    onSignInWithGoogle()
                                    onSuccess()
                                }
                            )
                            AuthButton(
                                logo = painterResource(id = if (LocalTheme.current) R.drawable.feature_settings_github_logo_dark else R.drawable.feature_settings_github_logo_light),
                                contentDescription = stringResource(id = R.string.feature_settings_sign_in_with_github),
                                text = stringResource(id = R.string.feature_settings_sign_in_with_github),
                                onClick = {
                                    onSignInWithGithub()
                                    onSuccess()
                                }
                            )

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
                                        color = authActionColor,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                            val context = LocalContext.current
                            TextButton(onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Verdenroz/verdax-market/blob/main/PRIVACY_POLICY.md"))
                                context.startActivity(intent)
                            }) {
                                Text(
                                    text = stringResource(R.string.feature_settings_privacy_policy),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = authActionColor
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
            onSuccess = {}
        )
    }
}

@Composable
fun AuthOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyBoardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyBoardAction: KeyboardActions = KeyboardActions.Default,
    isError: Boolean,
    errorText: String?,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        keyboardOptions = keyBoardOptions,
        keyboardActions = keyBoardAction,
        isError = isError,
        supportingText = {
            if (isError && errorText != null) {
                Text(
                    text = errorText,
                    color = MaterialTheme.colorScheme.error
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
        visualTransformation = visualTransformation,
        trailingIcon = trailingIcon,
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
private fun AuthButton(
    modifier: Modifier = Modifier,
    logo: Painter? = null,
    imageVector: ImageVector? = null,
    contentDescription: String? = null,
    text: String? = null,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    val isDarkTheme = LocalTheme.current
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
            .background(if (enabled) backgroundColor else Color.Gray)
            .border(1.dp, strokeColor, CircleShape)
            .clickable(onClick = onClick, enabled = enabled)
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