package com.verdenroz.verdaxmarket.network.model.indicators

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MacdResponse(
    @SerialName("MACD")
    val macd: Double,
    @SerialName("Signal")
    val signal: Double,
)