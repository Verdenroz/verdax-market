package com.verdenroz.verdaxmarket.core.data.repository

import com.verdenroz.verdaxmarket.core.common.enums.Interval
import com.verdenroz.verdaxmarket.core.common.error.DataError
import com.verdenroz.verdaxmarket.core.common.result.Result
import com.verdenroz.verdaxmarket.core.model.AnalysisSignal
import com.verdenroz.verdaxmarket.core.model.QuoteAnalysis
import com.verdenroz.verdaxmarket.core.model.indicators.TechnicalIndicator
import kotlinx.coroutines.flow.Flow

interface AnalysisSignalRepository {

    /**
     * Returns a map of [Interval] to [QuoteAnalysis] for the [symbol].
     * @param symbol The symbol of the stock.
     */
    suspend fun getAnalysisMap(symbol: String): Flow<Map<Interval, Result<QuoteAnalysis, DataError.Network>>>

    /**
     * Transforms the [QuoteAnalysis] from [getAnalysisMap] to a map of [TechnicalIndicator] to [AnalysisSignal].
     * @param symbol The symbol of the stock.
     * @param currentPrice The current price of the stock used to calculate moving average signal.
     */
    suspend fun getIntervalAnalysisSignalMap(symbol: String, currentPrice: Double): Flow<Map<Interval, Map<TechnicalIndicator, AnalysisSignal>>>

}