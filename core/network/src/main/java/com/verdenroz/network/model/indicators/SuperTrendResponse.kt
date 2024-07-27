package com.verdenroz.network.model.indicators

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SuperTrendResponse(
    @SerialName("Super Trend")
    val superTrend: Double,
    @SerialName("Trend")
    val trend: String
)
