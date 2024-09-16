package com.verdenroz.verdaxmarket.core.data.repository

import com.verdenroz.verdaxmarket.core.common.enums.Interval
import com.verdenroz.verdaxmarket.core.common.enums.TimePeriod
import com.verdenroz.verdaxmarket.core.common.error.DataError
import com.verdenroz.verdaxmarket.core.common.result.Result
import com.verdenroz.verdaxmarket.core.model.FullQuoteData
import com.verdenroz.verdaxmarket.core.model.HistoricalData
import com.verdenroz.verdaxmarket.core.model.MarketSector
import com.verdenroz.verdaxmarket.core.model.News
import com.verdenroz.verdaxmarket.core.model.QuoteAnalysis
import com.verdenroz.verdaxmarket.core.model.SimpleQuoteData
import kotlinx.coroutines.flow.Flow

interface QuoteRepository {

    /**
     * Get full quote data for a stock with all available information as [FullQuoteData]
     */
    fun getFullQuote(symbol: String): Flow<Result<FullQuoteData, DataError.Network>>

    /**
     * Get simple quote data for a stock with basic information as [SimpleQuoteData]
     */
    suspend fun getSimpleQuote(symbol: String): Flow<Result<SimpleQuoteData, DataError.Network>>

    /**
     * Get news for a symbol as a list of [News]
     */
    fun getNewsForSymbol(symbol: String): Flow<Result<List<News>, DataError.Network>>

    /**
     * Find similar stocks for a symbol as a list of [SimpleQuoteData]
     */
    fun getSimilarStocks(symbol: String): Flow<Result<List<SimpleQuoteData>, DataError.Network>>

    /**
     * Gets the [MarketSector] performance of the symbol if available
     */
    fun getSectorBySymbol(symbol: String): Flow<Result<MarketSector?, DataError.Network>>

    /**
     * Get historical data for a symbol as a map of dates to [HistoricalData]
     */
    suspend fun getTimeSeries(
        symbol: String,
        timePeriod: TimePeriod,
        interval: Interval
    ): Flow<Result<Map<String, HistoricalData>, DataError.Network>>

    /**
     * Get summary analysis for a symbol as [QuoteAnalysis] given an [Interval]
     */
    fun getAnalysis(
        symbol: String,
        interval: Interval
    ): Flow<Result<QuoteAnalysis?, DataError.Network>>

}