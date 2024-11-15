package com.verdenroz.verdaxmarket.core.data.repository

import com.verdenroz.verdaxmarket.core.model.ThemePreference
import com.verdenroz.verdaxmarket.core.model.UserSetting
import kotlinx.coroutines.flow.Flow

/**
 * Repository for user data
 */
interface UserDataRepository {

    /**
     * Flow of [UserSetting]
     */
    val userSetting: Flow<UserSetting>

    val areNotificationsEnabled: Flow<Boolean>

    /**
     * Sets the user's theme preference
     */
    suspend fun setThemePreference(themePreference: ThemePreference)

    /**
     * Sets whether notifications are enabled
     */
    suspend fun setNotificationsEnabled(notificationsEnabled: Boolean)

    /**
     * Sets whether to show tooltips on the UI
     */
    suspend fun setHintsEnabled(hintsEnabled: Boolean)

    /**
     * Sets whether market hours should be shown in the UI
     */
    suspend fun setShowMarketHours(showMarketHours: Boolean)

    /**
     * Sets whether user data should be sent anonymously for analytics
     */
    suspend fun setEnableAnonymousAnalytics(enableAnonymousAnalytics: Boolean)

    /**
     * Sets whether the user has completed onboarding
     */
    suspend fun setIsOnboardingComplete(isOnboardingComplete: Boolean)
}