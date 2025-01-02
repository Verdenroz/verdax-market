package com.verdenroz.verdaxmarket.core.model

data class UserSetting(
    val themePreference: ThemePreference = ThemePreference.SYSTEM,
    val regionPreference: RegionFilter = RegionFilter.US,
    val hintsEnabled: Boolean = true,
    val showMarketHours: Boolean = true,
    val isSynced: Boolean = true,
)