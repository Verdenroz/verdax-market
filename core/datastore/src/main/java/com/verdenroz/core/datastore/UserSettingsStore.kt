package com.verdenroz.core.datastore

import androidx.datastore.core.DataStore
import com.verdenroz.verdaxmarket.core.model.ThemePreference
import com.verdenroz.verdaxmarket.core.model.UserSetting
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserSettingsStore @Inject constructor(
    private val userSettingsDataStore: DataStore<UserSettings>
) {
    val userSettings = userSettingsDataStore.data
        .map {
            UserSetting(
                themePreference = when (it.themePreference) {
                    null,
                    UserSettings.ThemePreference.UNRECOGNIZED,
                    UserSettings.ThemePreference.SYSTEM -> ThemePreference.SYSTEM

                    UserSettings.ThemePreference.LIGHT -> ThemePreference.LIGHT
                    UserSettings.ThemePreference.DARK -> ThemePreference.DARK
                },
                notificationsEnabled = it.notificationsEnabled,
                hintsEnabled = it.hintsEnabled,
                showMarketHours = it.showMarketHours,
                isSynced = it.syncEnabled,
                enableAnonymousAnalytics = it.enableAnalytics,
                isOnboardingComplete = it.isOnboardingComplete
            )
        }

    suspend fun updateSettings(userSettings: UserSetting) {
        userSettingsDataStore.updateData {
            it.copy {
                this.themePreference = when (userSettings.themePreference) {
                    ThemePreference.SYSTEM -> UserSettings.ThemePreference.SYSTEM
                    ThemePreference.LIGHT -> UserSettings.ThemePreference.LIGHT
                    ThemePreference.DARK -> UserSettings.ThemePreference.DARK
                }
                this.notificationsEnabled = userSettings.notificationsEnabled
                this.hintsEnabled = userSettings.hintsEnabled
                this.showMarketHours = userSettings.showMarketHours
                this.syncEnabled = userSettings.isSynced
                this.enableAnalytics = userSettings.enableAnonymousAnalytics
                this.isOnboardingComplete = userSettings.isOnboardingComplete
            }
        }
    }

    suspend fun setThemePreference(themePreference: ThemePreference) {
        userSettingsDataStore.updateData {
            it.copy {
                this.themePreference = when (themePreference) {
                    ThemePreference.SYSTEM -> UserSettings.ThemePreference.SYSTEM
                    ThemePreference.LIGHT -> UserSettings.ThemePreference.LIGHT
                    ThemePreference.DARK -> UserSettings.ThemePreference.DARK
                }
            }
        }
    }

    suspend fun setNotificationsEnabled(notificationsEnabled: Boolean) {
        userSettingsDataStore.updateData {
            it.copy { this.notificationsEnabled = notificationsEnabled }
        }
    }

    suspend fun setHintsEnabled(hintsEnabled: Boolean) {
        userSettingsDataStore.updateData {
            it.copy { this.hintsEnabled = hintsEnabled }
        }
    }

    suspend fun setShowMarketHours(showMarketHours: Boolean) {
        userSettingsDataStore.updateData {
            it.copy { this.showMarketHours = showMarketHours }
        }
    }

    suspend fun setSync(isSynced: Boolean) {
        userSettingsDataStore.updateData {
            it.copy { this.syncEnabled = isSynced }
        }
    }

    suspend fun setEnableAnalytics(enableAnalytics: Boolean) {
        userSettingsDataStore.updateData {
            it.copy { this.enableAnalytics = enableAnalytics }
        }
    }

    suspend fun setIsOnboardingComplete(isOnboardingComplete: Boolean) {
        userSettingsDataStore.updateData {
            it.copy { this.isOnboardingComplete = isOnboardingComplete }
        }
    }
}