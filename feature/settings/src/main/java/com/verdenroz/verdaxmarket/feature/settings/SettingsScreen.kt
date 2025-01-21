package com.verdenroz.verdaxmarket.feature.settings

import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.verdenroz.verdaxmarket.core.designsystem.components.VxmBackIconButton
import com.verdenroz.verdaxmarket.core.designsystem.components.VxmCenterTopBar
import com.verdenroz.verdaxmarket.core.designsystem.icons.VxmIcons
import com.verdenroz.verdaxmarket.core.designsystem.theme.ThemePreviews
import com.verdenroz.verdaxmarket.core.designsystem.theme.VxmTheme
import com.verdenroz.verdaxmarket.core.designsystem.util.asUiText
import com.verdenroz.verdaxmarket.core.model.RegionFilter
import com.verdenroz.verdaxmarket.core.model.ThemePreference
import com.verdenroz.verdaxmarket.feature.settings.components.AccountSection
import com.verdenroz.verdaxmarket.feature.settings.components.RegionPreferenceItem
import com.verdenroz.verdaxmarket.feature.settings.components.SwitchSettingItem
import com.verdenroz.verdaxmarket.feature.settings.components.ThemePreferenceItem
import kotlinx.coroutines.launch

@Composable
fun SettingsRoute(
    onNavigateBack: () -> Unit,
    onShowSnackbar: suspend (String, String?, SnackbarDuration) -> Boolean,
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val settingsUiState by settingsViewModel.settingsUiState.collectAsStateWithLifecycle()
    val authState by authViewModel.authState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current as ComponentActivity
    val accountDeletedMessage = stringResource(id = R.string.feature_settings_account_deleted)

    LaunchedEffect(Unit) {
        authViewModel.authErrors.collect { errorMessage ->
            onShowSnackbar(
                errorMessage.asUiText().asString(context),
                null,
                SnackbarDuration.Short
            )
        }
    }

    SettingsScreen(
        settingsUiState = settingsUiState,
        authState = authState,
        onNavigateBack = onNavigateBack,
        onSignUpWithEmail = { email, password ->
            scope.launch {
                authViewModel.createAccountWithEmailPassword(email, password)
            }
        },
        onSignInWithEmail = { email, password ->
            scope.launch {
                authViewModel.signInWithEmailPassword(email, password)
            }
        },
        onSignInWithGoogle = {
            scope.launch {
                authViewModel.signInWithGoogle(context)
            }
        },
        onSignInWithGithub = {
            scope.launch {
                authViewModel.signInWithGitHub(context)
            }
        },
        onForgetPassword = { email ->
            scope.launch {
                authViewModel.resetPassword(email)
            }
        },
        onSignOut = {
            scope.launch {
                authViewModel.signOut()
            }
        },
        onDeleteAccount = { password ->
            scope.launch {
                val isSuccess = authViewModel.deleteAccount(context, password)
                if (isSuccess) {
                    Toast.makeText(context, accountDeletedMessage, Toast.LENGTH_SHORT).show()
                }
            }
        },
        onThemePreferenceChange = settingsViewModel::updateThemePreference,
        onRegionPreferenceChange = settingsViewModel::updateRegionPreference,
        onHintsSettingChange = settingsViewModel::updateHintsSetting,
        onShowMarketHoursChange = settingsViewModel::updateShowMarketHoursSetting,
        onSyncChange = settingsViewModel::updateSyncSetting,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingsScreen(
    settingsUiState: SettingsUiState,
    authState: UserAuthState,
    onNavigateBack: () -> Unit,
    onSignUpWithEmail: (String, String) -> Unit,
    onSignInWithEmail: (String, String) -> Unit,
    onSignInWithGoogle: () -> Unit,
    onSignInWithGithub: () -> Unit,
    onForgetPassword: (String) -> Unit,
    onSignOut: () -> Unit,
    onDeleteAccount: (String?) -> Unit,
    onThemePreferenceChange: (ThemePreference) -> Unit,
    onRegionPreferenceChange: (RegionFilter) -> Unit,
    onHintsSettingChange: (Boolean) -> Unit,
    onShowMarketHoursChange: (Boolean) -> Unit,
    onSyncChange: (Boolean) -> Unit,
) {
    when (settingsUiState) {
        SettingsUiState.Loading -> LoadingScreen()
        is SettingsUiState.Success -> {
            val settings = settingsUiState.settings
            Scaffold(
                topBar = {
                    VxmCenterTopBar(
                        navigationIcon = {
                            VxmBackIconButton(
                                onClick = onNavigateBack
                            )
                        },
                    )
                },
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ) { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                ) {
                    AccountSection(
                        userState = authState,
                        isSynced = settings.isSynced,
                        onSignUpWithEmail = onSignUpWithEmail,
                        onSignInWithEmail = onSignInWithEmail,
                        onSignInWithGoogle = onSignInWithGoogle,
                        onSignInWithGithub = onSignInWithGithub,
                        onForgetPassword = onForgetPassword,
                        onSignOut = onSignOut,
                        onDeleteAccount = onDeleteAccount,
                        onSyncChange = onSyncChange,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Appearance Section
                    SettingsSection(title = stringResource(id = R.string.feature_settings_appearance)) {
                        ThemePreferenceItem(
                            currentTheme = settings.themePreference,
                            onThemeChange = onThemePreferenceChange
                        )
                    }

                    // Region Section
                    SettingsSection(title = stringResource(id = R.string.feature_settings_location)) {
                        RegionPreferenceItem(
                            currentRegion = settings.regionPreference,
                            onRegionChange = onRegionPreferenceChange
                        )
                    }

                    // Display Preferences Section
                    SettingsSection(title = stringResource(id = R.string.feature_settings_display)) {
                        SwitchSettingItem(
                            title = stringResource(id = R.string.feature_settings_hints),
                            description = stringResource(id = R.string.feature_settings_hints_description),
                            icon = VxmIcons.Help,
                            checked = settings.hintsEnabled,
                            onCheckedChange = onHintsSettingChange
                        )
                    }

                    // Market Data Section
                    SettingsSection(title = stringResource(id = R.string.feature_settings_market_data)) {
                        SwitchSettingItem(
                            title = stringResource(id = R.string.feature_settings_market_hours),
                            description = stringResource(id = R.string.feature_settings_market_hours_description),
                            icon = VxmIcons.Schedule,
                            checked = settings.showMarketHours,
                            onCheckedChange = onShowMarketHoursChange
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        content()
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@ThemePreviews
@Composable
private fun PreviewSettingsScreen() {
    VxmTheme {
        SettingsScreen(
            settingsUiState = SettingsUiState.Success(
                settings = UserEditableSettings(
                    themePreference = ThemePreference.SYSTEM,
                    regionPreference = RegionFilter.US,
                    hintsEnabled = true,
                    showMarketHours = true,
                    isSynced = true,
                )
            ),
            authState = UserAuthState.SignedIn(
                displayName = "John Doe",
                email = "john@gmail.com",
                photoUrl = "https://example.com/profile.jpg",
                creationDate = "November 16, 2021",
                providerId = "google.com",
            ),
            onSignUpWithEmail = { _, _ -> },
            onSignInWithEmail = { _, _ -> },
            onSignInWithGoogle = { },
            onSignInWithGithub = { },
            onForgetPassword = { },
            onSignOut = { },
            onDeleteAccount = { },
            onNavigateBack = { },
            onThemePreferenceChange = { },
            onRegionPreferenceChange = { },
            onHintsSettingChange = { },
            onShowMarketHoursChange = { },
            onSyncChange = { },
        )
    }
}