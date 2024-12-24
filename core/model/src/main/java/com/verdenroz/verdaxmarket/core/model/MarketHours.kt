package com.verdenroz.verdaxmarket.core.model

/**
 * Represents the status of the market at present.
 * @param status The current [MarketStatus].
 * @param reason The reason for the current [MarketStatus].
 */
data class MarketHours(
    val status: MarketStatus,
    val reason: String,
)
