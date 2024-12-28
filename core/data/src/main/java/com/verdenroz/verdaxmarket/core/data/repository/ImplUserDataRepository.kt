package com.verdenroz.verdaxmarket.core.data.repository

import com.verdenroz.core.datastore.UserSettingsStore
import com.verdenroz.core.notifications.NotificationManager
import com.verdenroz.verdaxmarket.core.model.ThemePreference
import com.verdenroz.verdaxmarket.core.model.UserSetting
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ImplUserDataRepository @Inject constructor(
    private val userSettingsStore: UserSettingsStore,
    private val notificationManager: NotificationManager
) : UserDataRepository {

    override val userSetting: Flow<UserSetting> = userSettingsStore.userSettings

    override val areNotificationsEnabled: Flow<Boolean> =
        notificationManager.areNotificationsEnabled

    override suspend fun setThemePreference(themePreference: ThemePreference) =
        userSettingsStore.setThemePreference(themePreference)

    override suspend fun setNotificationsEnabled(notificationsEnabled: Boolean) =
        notificationManager.updateNotificationSettings(notificationsEnabled)

    override suspend fun setHintsEnabled(hintsEnabled: Boolean) =
        userSettingsStore.setHintsEnabled(hintsEnabled)

    override suspend fun setShowMarketHours(showMarketHours: Boolean) =
        userSettingsStore.setShowMarketHours(showMarketHours)

    override suspend fun setSync(isSynced: Boolean) =
        userSettingsStore.setSync(isSynced)

    override suspend fun setEnableAnonymousAnalytics(enableAnonymousAnalytics: Boolean) =
        userSettingsStore.setEnableAnalytics(enableAnonymousAnalytics)

    override suspend fun setIsOnboardingComplete(isOnboardingComplete: Boolean) =
        userSettingsStore.setIsOnboardingComplete(isOnboardingComplete)
}