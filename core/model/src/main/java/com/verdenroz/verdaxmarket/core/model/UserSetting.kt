package com.verdenroz.verdaxmarket.core.model

import com.verdenroz.verdaxmarket.core.model.enums.RegionFilter
import com.verdenroz.verdaxmarket.core.model.enums.ThemePreference

data class UserSetting(
    val themePreference: ThemePreference = ThemePreference.SYSTEM,
    val regionPreference: RegionFilter = RegionFilter.US,
    val hintsEnabled: Boolean = true,
    val showMarketHours: Boolean = true,
    val isSynced: Boolean = true,
)