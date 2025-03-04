package com.verdenroz.core.datastore

import androidx.datastore.core.DataStore
import com.verdenroz.verdaxmarket.core.model.UserSetting
import com.verdenroz.verdaxmarket.core.model.enums.IndexTimePeriodPreference
import com.verdenroz.verdaxmarket.core.model.enums.RegionFilter
import com.verdenroz.verdaxmarket.core.model.enums.SectorTimePeriodPreference
import com.verdenroz.verdaxmarket.core.model.enums.ThemePreference
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
                regionPreference = when (it.regionPreference) {
                    null,
                    UserSettings.RegionPreference.UNRECOGNIZED,
                    UserSettings.RegionPreference.US -> RegionFilter.US
                    UserSettings.RegionPreference.NA -> RegionFilter.NA
                    UserSettings.RegionPreference.SA -> RegionFilter.SA
                    UserSettings.RegionPreference.EU -> RegionFilter.EU
                    UserSettings.RegionPreference.AS -> RegionFilter.AS
                    UserSettings.RegionPreference.AF -> RegionFilter.AF
                    UserSettings.RegionPreference.AU -> RegionFilter.OCE
                    UserSettings.RegionPreference.ME -> RegionFilter.ME
                    UserSettings.RegionPreference.GLOBAL -> RegionFilter.GLOBAL
                },
                indexTimePeriodPreference = when (it.indexTimePeriodPreference) {
                    null,
                    UserSettings.IndexTimePeriodPreference.UNRECOGNIZED,
                    UserSettings.IndexTimePeriodPreference.ONE_DAY_INDEX_PERIOD -> IndexTimePeriodPreference.ONE_DAY
                    UserSettings.IndexTimePeriodPreference.FIVE_DAY_INDEX_PERIOD -> IndexTimePeriodPreference.FIVE_DAY
                    UserSettings.IndexTimePeriodPreference.ONE_MONTH_INDEX_PERIOD -> IndexTimePeriodPreference.ONE_MONTH
                    UserSettings.IndexTimePeriodPreference.SIX_MONTH_INDEX_PERIOD -> IndexTimePeriodPreference.SIX_MONTH
                    UserSettings.IndexTimePeriodPreference.YEAR_TO_DATE_INDEX_PERIOD -> IndexTimePeriodPreference.YEAR_TO_DATE
                    UserSettings.IndexTimePeriodPreference.ONE_YEAR_INDEX_PERIOD -> IndexTimePeriodPreference.ONE_YEAR
                    UserSettings.IndexTimePeriodPreference.FIVE_YEAR_INDEX_PERIOD -> IndexTimePeriodPreference.FIVE_YEAR
                },
                sectorTimePeriodPreference = when (it.sectorTimePeriodPreference) {
                    null,
                    UserSettings.SectorTimePeriodPreference.UNRECOGNIZED,
                    UserSettings.SectorTimePeriodPreference.ONE_DAY_SECTOR_PERIOD -> SectorTimePeriodPreference.ONE_DAY
                    UserSettings.SectorTimePeriodPreference.YEAR_TO_DATE_SECTOR_PERIOD -> SectorTimePeriodPreference.YEAR_TO_DATE
                    UserSettings.SectorTimePeriodPreference.ONE_YEAR_SECTOR_PERIOD -> SectorTimePeriodPreference.ONE_YEAR
                    UserSettings.SectorTimePeriodPreference.FIVE_YEAR_SECTOR_PERIOD -> SectorTimePeriodPreference.FIVE_YEAR
                },
                hintsEnabled = it.hintsEnabled,
                showMarketHours = it.showMarketHours,
                isSynced = it.syncEnabled,
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
                this.regionPreference = when (userSettings.regionPreference) {
                    RegionFilter.US -> UserSettings.RegionPreference.US
                    RegionFilter.NA -> UserSettings.RegionPreference.NA
                    RegionFilter.SA -> UserSettings.RegionPreference.SA
                    RegionFilter.EU -> UserSettings.RegionPreference.EU
                    RegionFilter.AS -> UserSettings.RegionPreference.AS
                    RegionFilter.AF -> UserSettings.RegionPreference.AF
                    RegionFilter.OCE -> UserSettings.RegionPreference.AU
                    RegionFilter.ME -> UserSettings.RegionPreference.ME
                    RegionFilter.GLOBAL -> UserSettings.RegionPreference.GLOBAL
                }
                this.hintsEnabled = userSettings.hintsEnabled
                this.showMarketHours = userSettings.showMarketHours
                this.syncEnabled = userSettings.isSynced
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

    suspend fun setRegionPreference(regionPreference: RegionFilter) {
        userSettingsDataStore.updateData {
            it.copy {
                this.regionPreference = when (regionPreference) {
                    RegionFilter.US -> UserSettings.RegionPreference.US
                    RegionFilter.NA -> UserSettings.RegionPreference.NA
                    RegionFilter.SA -> UserSettings.RegionPreference.SA
                    RegionFilter.EU -> UserSettings.RegionPreference.EU
                    RegionFilter.AS -> UserSettings.RegionPreference.AS
                    RegionFilter.AF -> UserSettings.RegionPreference.AF
                    RegionFilter.OCE -> UserSettings.RegionPreference.AU
                    RegionFilter.ME -> UserSettings.RegionPreference.ME
                    RegionFilter.GLOBAL -> UserSettings.RegionPreference.GLOBAL
                }
            }
        }
    }

    suspend fun setIndexTimePeriodPreference(indexTimePeriodPreference: IndexTimePeriodPreference) {
        userSettingsDataStore.updateData {
            it.copy {
                this.indexTimePeriodPreference = when (indexTimePeriodPreference) {
                    IndexTimePeriodPreference.ONE_DAY -> UserSettings.IndexTimePeriodPreference.ONE_DAY_INDEX_PERIOD
                    IndexTimePeriodPreference.FIVE_DAY -> UserSettings.IndexTimePeriodPreference.FIVE_DAY_INDEX_PERIOD
                    IndexTimePeriodPreference.ONE_MONTH -> UserSettings.IndexTimePeriodPreference.ONE_MONTH_INDEX_PERIOD
                    IndexTimePeriodPreference.SIX_MONTH -> UserSettings.IndexTimePeriodPreference.SIX_MONTH_INDEX_PERIOD
                    IndexTimePeriodPreference.YEAR_TO_DATE -> UserSettings.IndexTimePeriodPreference.YEAR_TO_DATE_INDEX_PERIOD
                    IndexTimePeriodPreference.ONE_YEAR -> UserSettings.IndexTimePeriodPreference.ONE_YEAR_INDEX_PERIOD
                    IndexTimePeriodPreference.FIVE_YEAR -> UserSettings.IndexTimePeriodPreference.FIVE_YEAR_INDEX_PERIOD
                }
            }
        }
    }

    suspend fun setSectorTimePeriodPreference(sectorTimePeriodPreference: SectorTimePeriodPreference) {
        userSettingsDataStore.updateData {
            it.copy {
                this.sectorTimePeriodPreference = when (sectorTimePeriodPreference) {
                    SectorTimePeriodPreference.ONE_DAY -> UserSettings.SectorTimePeriodPreference.ONE_DAY_SECTOR_PERIOD
                    SectorTimePeriodPreference.YEAR_TO_DATE -> UserSettings.SectorTimePeriodPreference.YEAR_TO_DATE_SECTOR_PERIOD
                    SectorTimePeriodPreference.ONE_YEAR -> UserSettings.SectorTimePeriodPreference.ONE_YEAR_SECTOR_PERIOD
                    SectorTimePeriodPreference.FIVE_YEAR ->  UserSettings.SectorTimePeriodPreference.FIVE_YEAR_SECTOR_PERIOD
                }
            }
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
}