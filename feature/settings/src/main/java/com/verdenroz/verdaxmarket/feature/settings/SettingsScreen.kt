package com.verdenroz.verdaxmarket.feature.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.verdenroz.verdaxmarket.core.designsystem.components.VxmTopBar
import com.verdenroz.verdaxmarket.core.designsystem.icons.VxmIcons
import com.verdenroz.verdaxmarket.core.designsystem.theme.ThemePreviews
import com.verdenroz.verdaxmarket.core.designsystem.theme.VxmTheme
import com.verdenroz.verdaxmarket.core.model.ThemePreference
import com.verdenroz.verdaxmarket.feature.settings.components.SwitchSettingItem
import com.verdenroz.verdaxmarket.feature.settings.components.ThemePreferenceItem

@Composable
fun SettingsRoute(
    onNavigateBack: () -> Unit,
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val settingsUiState by settingsViewModel.settingsUiState.collectAsStateWithLifecycle()

    SettingsScreen(
        settingsUiState = settingsUiState,
        onNavigateBack = onNavigateBack,
        onThemePreferenceChange = settingsViewModel::updateThemePreference,
        onNotificationSettingChange = settingsViewModel::updateNotificationSetting,
        onHintsSettingChange = settingsViewModel::updateHintsSetting,
        onShowMarketHoursChange = settingsViewModel::updateShowMarketHoursSetting,
        onEnableAnonymousAnalyticsChange = settingsViewModel::updateEnableAnonymousAnalyticsSetting,
    )
}

@Composable
internal fun SettingsScreen(
    settingsUiState: SettingsUiState,
    onNavigateBack: () -> Unit,
    onThemePreferenceChange: (ThemePreference) -> Unit,
    onNotificationSettingChange: (Boolean) -> Unit,
    onHintsSettingChange: (Boolean) -> Unit,
    onShowMarketHoursChange: (Boolean) -> Unit,
    onEnableAnonymousAnalyticsChange: (Boolean) -> Unit,
) {
    when (settingsUiState) {
        SettingsUiState.Loading -> LoadingScreen()
        is SettingsUiState.Success -> {
            val settings = settingsUiState.settings
            Scaffold(
                topBar = {
                    VxmTopBar(
                        navigationIcon = {
                            IconButton(onClick = onNavigateBack) {
                                Icon(
                                    imageVector = VxmIcons.ArrowBack,
                                    contentDescription = stringResource(id = R.string.feature_settings_back)
                                )
                            }
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
                    // Appearance Section
                    SettingsSection(title = stringResource(id = R.string.feature_settings_appearane)) {
                        ThemePreferenceItem(
                            currentTheme = settings.themePreference,
                            onThemeChange = onThemePreferenceChange
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

                    // Notifications Section
                    SettingsSection(title = stringResource(id = R.string.feature_settings_notifications)) {
                        SwitchSettingItem(
                            title = stringResource(id = R.string.feature_settings_notifications_title),
                            description = stringResource(id = R.string.feature_settings_notifications_description),
                            icon = VxmIcons.Notification,
                            checked = settings.notificationsEnabled,
                            onCheckedChange = onNotificationSettingChange
                        )
                    }

                    // Privacy Section
                    SettingsSection(title = stringResource(id = R.string.feature_settings_privacy)) {
                        SwitchSettingItem(
                            title = stringResource(id = R.string.feature_settings_analytics),
                            description = stringResource(id = R.string.feature_settings_analytics_description),
                            icon = VxmIcons.Analytics,
                            checked = settings.enableAnonymousAnalytics,
                            onCheckedChange = onEnableAnonymousAnalyticsChange
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
                    notificationsEnabled = true,
                    hintsEnabled = true,
                    showMarketHours = true,
                    enableAnonymousAnalytics = true,
                )
            ),
            onNavigateBack = {},
            onThemePreferenceChange = {},
            onNotificationSettingChange = {},
            onHintsSettingChange = {},
            onShowMarketHoursChange = {},
            onEnableAnonymousAnalyticsChange = {},
        )
    }
}