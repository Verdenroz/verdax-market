package com.verdenroz.network.model.indicators

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BBandsResponse(
    @SerialName("Upper Band")
    val upperBand: Double,
    @SerialName("Lower Band")
    val lowerBand: Double
)