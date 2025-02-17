package com.verdenroz.verdaxmarket.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class HistoricalDataResponse(
    val open: Float,
    val high: Float,
    val low: Float,
    val close: Float,
    val volume: Long,
    val adjClose: Float? = null
)