package com.verdenroz.verdaxmarket.core.model

import com.verdenroz.verdaxmarket.core.model.enums.RegionFilter
import com.verdenroz.verdaxmarket.core.model.enums.ThemePreference
import com.verdenroz.verdaxmarket.core.model.enums.TimePeriodPreference

data class UserSetting(
    val themePreference: ThemePreference = ThemePreference.SYSTEM,
    val regionPreference: RegionFilter = RegionFilter.US,
    val indexTimePeriodPreference: TimePeriodPreference = TimePeriodPreference.ONE_DAY,
    val sectorTimePeriodPreference: TimePeriodPreference = TimePeriodPreference.SIX_MONTH,
    val hintsEnabled: Boolean = true,
    val showMarketHours: Boolean = true,
    val isSynced: Boolean = true,
)