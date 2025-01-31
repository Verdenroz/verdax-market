package com.verdenroz.verdaxmarket.core.model

import com.verdenroz.verdaxmarket.core.model.enums.Sector

/**
 * Local data class for sector information
 * @param sector the [com.verdenroz.verdaxmarket.core.model.enums.Sector] of the market
 * @param dayReturn the return for the day
 * @param ytdReturn the return year to date
 * @param yearReturn the return for the year
 * @param threeYearReturn the return for the past three years
 * @param fiveYearReturn the return for the past five years
 */
data class MarketSector(
    val sector: Sector,
    val dayReturn: String,
    val ytdReturn: String,
    val yearReturn: String,
    val threeYearReturn: String,
    val fiveYearReturn: String,
)
