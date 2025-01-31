package com.verdenroz.verdaxmarket.core.model

import com.verdenroz.verdaxmarket.core.model.enums.IndexTimePeriodPreference
import com.verdenroz.verdaxmarket.core.model.enums.RegionFilter
import com.verdenroz.verdaxmarket.core.model.enums.SectorTimePeriodPreference
import com.verdenroz.verdaxmarket.core.model.enums.ThemePreference

data class UserSetting(
    val themePreference: ThemePreference = ThemePreference.SYSTEM,
    val regionPreference: RegionFilter = RegionFilter.US,
    val indexTimePeriodPreference: IndexTimePeriodPreference = IndexTimePeriodPreference.ONE_DAY,
    val sectorIndexTimePeriodPreference: SectorTimePeriodPreference = SectorTimePeriodPreference.ONE_YEAR,
    val hintsEnabled: Boolean = true,
    val showMarketHours: Boolean = true,
    val isSynced: Boolean = true,
)