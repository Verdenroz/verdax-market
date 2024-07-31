package com.verdenroz.verdaxmarket.core.data.repository

import com.verdenroz.verdaxmarket.core.common.error.DataError
import com.verdenroz.verdaxmarket.core.common.result.Result
import com.verdenroz.verdaxmarket.core.model.MarketIndex
import com.verdenroz.verdaxmarket.core.model.MarketMover
import com.verdenroz.verdaxmarket.core.model.MarketSector
import com.verdenroz.verdaxmarket.core.model.News
import kotlinx.coroutines.flow.Flow

interface MarketInfoRepository {

    companion object {
        /**
         * Refresh interval for [indices], [actives], [losers], [gainers] when the market is open
         */
        internal const val MARKET_DATA_REFRESH_OPEN = 20000L // 20 seconds
        /**
         * Refresh interval for [indices], [actives], [losers], [gainers] when the market is closed
         */
        internal const val MARKET_DATA_REFRESH_CLOSED = 600000L // 10 minutes

        /**
         * Refresh interval for [sectors] and [headlines] when the market is open
         */
        internal const val SLOW_REFRESH_INTERVAL_OPEN = 1800000L // 30 minutes

        /**
         * Refresh interval for [headlines] when the market is closed
         */
        internal const val SLOW_REFRESH_INTERVAL_CLOSED = 3600000L // 1 hr

        /**
         * Refresh interval for [sectors] when the market is closed
         */
        internal const val NEVER_REFRESH_INTERVAL = Long.MAX_VALUE
    }

    /**
     * The current market status of either open or closed
     */
    val isOpen: Flow<Boolean>

    /**
     *  Market indices as list of [MarketIndex]
     */
    val indices: Flow<Result<List<MarketIndex>, DataError.Network>>

    /**
     * Active stocks as list of [MarketMover]
     */
    val actives: Flow<Result<List<MarketMover>, DataError.Network>>

    /**
     * Losers as list of [MarketMover]
     */
    val losers: Flow<Result<List<MarketMover>, DataError.Network>>

    /**
     * Gainers as list of [MarketMover]
     */
    val gainers: Flow<Result<List<MarketMover>, DataError.Network>>

    /**
     * Latest news headlines as list of [News]
     */
    val headlines: Flow<Result<List<News>, DataError.Network>>

    /**
     * Sectors as list of [MarketSector]
     */
    val sectors: Flow<Result<List<MarketSector>, DataError.Network>>

}