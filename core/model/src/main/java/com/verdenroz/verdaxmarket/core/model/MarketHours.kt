package com.verdenroz.verdaxmarket.core.model

import com.verdenroz.verdaxmarket.core.model.enums.MarketStatus
import com.verdenroz.verdaxmarket.core.model.enums.MarketStatusReason

/**
 * Represents the status of the market at present.
 * @param status The current [com.verdenroz.verdaxmarket.core.model.enums.MarketStatus].
 * @param reason The [com.verdenroz.verdaxmarket.core.model.enums.MarketStatusReason] for the current [com.verdenroz.verdaxmarket.core.model.enums.MarketStatus].
 */
data class MarketHours(
    val status: MarketStatus,
    val reason: MarketStatusReason,
)
