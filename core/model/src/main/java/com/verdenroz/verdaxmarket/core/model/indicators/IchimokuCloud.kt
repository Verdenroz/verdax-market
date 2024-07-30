package com.verdenroz.verdaxmarket.core.model.indicators

data class IchimokuCloud(
    val conversionLine: Double,
    val baseLine: Double,
    val laggingSpan: Double?,
    val leadingSpanA: Double?,
    val leadingSpanB: Double?
)