package com.verdenroz.verdaxmarket.core.model

/**
 * Represents the status of the market at present.
 * @param status The current [MarketStatus].
 * @param reason The [MarketStatusReason] for the current [MarketStatus].
 */
data class MarketHours(
    val status: MarketStatus,
    val reason: MarketStatusReason,
)
