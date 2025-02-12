package com.verdenroz.verdaxmarket.core.data.repository

import com.verdenroz.core.datastore.UserSettingsStore
import com.verdenroz.verdaxmarket.core.model.UserSetting
import com.verdenroz.verdaxmarket.core.model.enums.IndexTimePeriodPreference
import com.verdenroz.verdaxmarket.core.model.enums.RegionFilter
import com.verdenroz.verdaxmarket.core.model.enums.SectorTimePeriodPreference
import com.verdenroz.verdaxmarket.core.model.enums.ThemePreference
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ImplUserDataRepository @Inject constructor(
    private val userSettingsStore: UserSettingsStore,
) : UserDataRepository {

    override val userSetting: Flow<UserSetting> = userSettingsStore.userSettings

    override suspend fun setThemePreference(themePreference: ThemePreference) =
        userSettingsStore.setThemePreference(themePreference)

    override suspend fun setRegionPreference(regionPreference: RegionFilter) =
        userSettingsStore.setRegionPreference(regionPreference)

    override suspend fun setIndexTimePeriodPreference(indexTimePeriodPreference: IndexTimePeriodPreference) {
        userSettingsStore.setIndexTimePeriodPreference(indexTimePeriodPreference)
    }

    override suspend fun setSectorTimePeriodPreference(sectorTimePeriodPreference: SectorTimePeriodPreference) {
        userSettingsStore.setSectorTimePeriodPreference(sectorTimePeriodPreference)
    }

    override suspend fun setHintsEnabled(hintsEnabled: Boolean) =
        userSettingsStore.setHintsEnabled(hintsEnabled)

    override suspend fun setShowMarketHours(showMarketHours: Boolean) =
        userSettingsStore.setShowMarketHours(showMarketHours)

    override suspend fun setSync(isSynced: Boolean) =
        userSettingsStore.setSync(isSynced)
}