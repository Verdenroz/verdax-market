package com.verdenroz.core.datastore

import androidx.datastore.core.DataStore
import com.verdenroz.verdaxmarket.core.model.UserSetting
import com.verdenroz.verdaxmarket.core.model.enums.RegionFilter
import com.verdenroz.verdaxmarket.core.model.enums.ThemePreference
import com.verdenroz.verdaxmarket.core.model.enums.TimePeriodPreference
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
                    UserSettings.RegionPreference.AU -> RegionFilter.AU
                    UserSettings.RegionPreference.ME -> RegionFilter.ME
                    UserSettings.RegionPreference.GLOBAL -> RegionFilter.GLOBAL
                },
                indexTimePeriodPreference = when (it.indexTimePeriodPreference) {
                    null,
                    UserSettings.TimePeriodPreference.UNRECOGNIZED,
                    UserSettings.TimePeriodPreference.ONE_DAY -> TimePeriodPreference.ONE_DAY
                    UserSettings.TimePeriodPreference.FIVE_DAYS -> TimePeriodPreference.FIVE_DAY
                    UserSettings.TimePeriodPreference.ONE_MONTH -> TimePeriodPreference.ONE_MONTH
                    UserSettings.TimePeriodPreference.SIX_MONTHS -> TimePeriodPreference.SIX_MONTH
                    UserSettings.TimePeriodPreference.YEAR_TO_DATE -> TimePeriodPreference.YEAR_TO_DATE
                    UserSettings.TimePeriodPreference.ONE_YEAR -> TimePeriodPreference.ONE_YEAR
                    UserSettings.TimePeriodPreference.FIVE_YEARS -> TimePeriodPreference.FIVE_YEAR
                },
                sectorTimePeriodPreference = when (it.sectorTimePeriodPreference) {
                    null,
                    UserSettings.TimePeriodPreference.UNRECOGNIZED,
                    UserSettings.TimePeriodPreference.ONE_DAY -> TimePeriodPreference.ONE_DAY
                    UserSettings.TimePeriodPreference.FIVE_DAYS -> TimePeriodPreference.FIVE_DAY
                    UserSettings.TimePeriodPreference.ONE_MONTH -> TimePeriodPreference.ONE_MONTH
                    UserSettings.TimePeriodPreference.SIX_MONTHS -> TimePeriodPreference.SIX_MONTH
                    UserSettings.TimePeriodPreference.YEAR_TO_DATE -> TimePeriodPreference.YEAR_TO_DATE
                    UserSettings.TimePeriodPreference.ONE_YEAR -> TimePeriodPreference.ONE_YEAR
                    UserSettings.TimePeriodPreference.FIVE_YEARS -> TimePeriodPreference.FIVE_YEAR
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
                    RegionFilter.AU -> UserSettings.RegionPreference.AU
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
                    RegionFilter.AU -> UserSettings.RegionPreference.AU
                    RegionFilter.ME -> UserSettings.RegionPreference.ME
                    RegionFilter.GLOBAL -> UserSettings.RegionPreference.GLOBAL
                }
            }
        }
    }

    suspend fun setIndexTimePeriodPreference(timePeriodPreference: TimePeriodPreference) {
        userSettingsDataStore.updateData {
            it.copy {
                this.indexTimePeriodPreference = when (timePeriodPreference) {
                    TimePeriodPreference.ONE_DAY -> UserSettings.TimePeriodPreference.ONE_DAY
                    TimePeriodPreference.FIVE_DAY -> UserSettings.TimePeriodPreference.FIVE_DAYS
                    TimePeriodPreference.ONE_MONTH -> UserSettings.TimePeriodPreference.ONE_MONTH
                    TimePeriodPreference.SIX_MONTH -> UserSettings.TimePeriodPreference.SIX_MONTHS
                    TimePeriodPreference.YEAR_TO_DATE -> UserSettings.TimePeriodPreference.YEAR_TO_DATE
                    TimePeriodPreference.ONE_YEAR -> UserSettings.TimePeriodPreference.ONE_YEAR
                    TimePeriodPreference.FIVE_YEAR -> UserSettings.TimePeriodPreference.FIVE_YEARS
                }
            }
        }
    }

    suspend fun setSectorTimePeriodPreference(timePeriodPreference: TimePeriodPreference) {
        userSettingsDataStore.updateData {
            it.copy {
                this.sectorTimePeriodPreference = when (timePeriodPreference) {
                    TimePeriodPreference.ONE_DAY -> UserSettings.TimePeriodPreference.ONE_DAY
                    TimePeriodPreference.FIVE_DAY -> UserSettings.TimePeriodPreference.FIVE_DAYS
                    TimePeriodPreference.ONE_MONTH -> UserSettings.TimePeriodPreference.ONE_MONTH
                    TimePeriodPreference.SIX_MONTH -> UserSettings.TimePeriodPreference.SIX_MONTHS
                    TimePeriodPreference.YEAR_TO_DATE -> UserSettings.TimePeriodPreference.YEAR_TO_DATE
                    TimePeriodPreference.ONE_YEAR -> UserSettings.TimePeriodPreference.ONE_YEAR
                    TimePeriodPreference.FIVE_YEAR -> UserSettings.TimePeriodPreference.FIVE_YEARS
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