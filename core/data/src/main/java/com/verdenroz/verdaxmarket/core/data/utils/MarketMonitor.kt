package com.verdenroz.verdaxmarket.core.data.utils

import com.verdenroz.verdaxmarket.core.model.MarketHours
import kotlinx.coroutines.flow.Flow

/**
 * Utility for reporting market open status
 */
interface MarketMonitor {
    val marketHours: Flow<MarketHours>
}