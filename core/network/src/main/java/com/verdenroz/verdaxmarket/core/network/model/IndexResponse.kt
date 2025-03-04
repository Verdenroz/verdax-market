package com.verdenroz.verdaxmarket.core.network.model

import kotlinx.serialization.Serializable

/**
 * Data response for stock market indices
 * @param name Name of the index
 * @param value Current value of the index
 * @param change Change in value the index
 * @param percentChange Percent change in value of the index
 */
@Serializable
data class IndexResponse(
    val name: String,
    val value: Double,
    val change: String,
    val percentChange: String,
    val fiveDaysReturn: String? = null,
    val oneMonthReturn: String? = null,
    val sixMonthReturn: String? = null,
    val ytdReturn: String? = null,
    val yearReturn: String? = null,
    val fiveYearReturn: String? = null,
)
