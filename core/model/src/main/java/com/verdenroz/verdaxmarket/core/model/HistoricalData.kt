package com.verdenroz.verdaxmarket.core.model

/**
 * Local data class for a specific stock's OHLCV data at some date in time
 */
data class HistoricalData(
    val open: Float,
    val high: Float,
    val low: Float,
    val close: Float,
    val volume: String
)