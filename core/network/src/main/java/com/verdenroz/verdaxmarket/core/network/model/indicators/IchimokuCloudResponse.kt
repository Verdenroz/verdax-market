package com.verdenroz.verdaxmarket.core.network.model.indicators

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IchimokuCloudResponse(
    @SerialName("Conversion Line")
    val conversionLine: Double,
    @SerialName("Base Line")
    val baseLine: Double,
    @SerialName("Lagging Span")
    val laggingSpan: Double? = null,
    @SerialName("Leading Span A")
    val leadingSpanA: Double? = null,
    @SerialName("Leading Span B")
    val leadingSpanB: Double? = null
)
