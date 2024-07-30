package com.verdenroz.verdaxmarket.core.network.model.indicators

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AroonResponse(
    @SerialName("Aroon Up")
    val aroonUp: Double,
    @SerialName("Aroon Down")
    val aroonDown: Double
)
