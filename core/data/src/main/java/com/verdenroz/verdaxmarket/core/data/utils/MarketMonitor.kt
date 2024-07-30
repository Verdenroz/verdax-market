package com.verdenroz.verdaxmarket.core.data.utils

import kotlinx.coroutines.flow.Flow

/**
 * Utility for reporting market open status
 */
interface MarketMonitor {
    val isMarketOpen: Flow<Boolean>
}