package com.verdenroz.verdaxmarket.core.model

data class UserSetting(
    val themePreference: ThemePreference = ThemePreference.SYSTEM,
    val notificationsEnabled: Boolean = true,
    val hintsEnabled: Boolean = true,
    val showMarketHours: Boolean = true,
    val isSynced: Boolean = true,
    val enableAnonymousAnalytics: Boolean = true,
    val isOnboardingComplete: Boolean = false
)