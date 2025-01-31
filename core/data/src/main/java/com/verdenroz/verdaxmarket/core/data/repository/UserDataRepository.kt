package com.verdenroz.verdaxmarket.core.data.repository

import com.verdenroz.verdaxmarket.core.model.UserSetting
import com.verdenroz.verdaxmarket.core.model.enums.RegionFilter
import com.verdenroz.verdaxmarket.core.model.enums.ThemePreference
import com.verdenroz.verdaxmarket.core.model.enums.TimePeriodPreference
import kotlinx.coroutines.flow.Flow

/**
 * Repository for user data
 */
interface UserDataRepository {

    /**
     * Flow of [UserSetting]
     */
    val userSetting: Flow<UserSetting>

    /**
     * Sets the user's theme preference
     */
    suspend fun setThemePreference(themePreference: ThemePreference)

    /**
     * Sets whether notifications are enabled
     */
    suspend fun setRegionPreference(regionPreference: RegionFilter)

    /**
     * Sets the preferred time period for market index performance
     */
    suspend fun setIndexTimePeriodPreference(timePeriodPreference: TimePeriodPreference)

    /**
     * Sets the preferred time period for sector performance
     */
    suspend fun setSectorTimePeriodPreference(timePeriodPreference: TimePeriodPreference)

    /**
     * Sets whether to show tooltips on the UI
     */
    suspend fun setHintsEnabled(hintsEnabled: Boolean)

    /**
     * Sets whether market hours should be shown in the UI
     */
    suspend fun setShowMarketHours(showMarketHours: Boolean)

    /**
     * Sets whether user data should be synced with cloud
     */
    suspend fun setSync(isSynced: Boolean)
}