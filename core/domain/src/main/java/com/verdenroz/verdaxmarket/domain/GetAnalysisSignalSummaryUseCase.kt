package com.verdenroz.verdaxmarket.domain

import com.verdenroz.verdaxmarket.core.common.dispatchers.Dispatcher
import com.verdenroz.verdaxmarket.core.common.dispatchers.FinanceQueryDispatchers
import com.verdenroz.verdaxmarket.core.common.enums.Interval
import com.verdenroz.verdaxmarket.core.common.error.DataError
import com.verdenroz.verdaxmarket.core.common.result.Result
import com.verdenroz.verdaxmarket.core.model.AnalysisSignal
import com.verdenroz.verdaxmarket.core.model.AnalysisSignalSummary
import com.verdenroz.verdaxmarket.core.model.QuoteSignal
import com.verdenroz.verdaxmarket.core.model.indicators.IndicatorType
import com.verdenroz.verdaxmarket.core.model.indicators.TechnicalIndicator
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

/**
 * Use case that calculates the summary of the signals for a given set of indicators
 */
class GetAnalysisSignalSummaryUseCase @Inject constructor(
    @Dispatcher(FinanceQueryDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) {

    /**
     * Gets the [AnalysisSignalSummary] for each [IndicatorType] in every [Interval]
     */
    operator fun invoke(
        signals: Flow<Map<Interval, Result<Map<TechnicalIndicator, AnalysisSignal>, DataError.Network>>>,
    ): Flow<Map<Interval, Result<Map<IndicatorType, AnalysisSignalSummary>, DataError.Network>>> = signals.flatMapLatest { analysisSignal ->
        flow {
            val signalSummaryMap = ConcurrentHashMap<Interval, Result<Map<IndicatorType, AnalysisSignalSummary>, DataError.Network>>()
            withContext(ioDispatcher) {
                analysisSignal.map { (interval, signalMap) ->
                    async {
                        when (signalMap) {
                            is Result.Success -> {
                                val movingAverageSummary = calculateSummary(signalMap.data.filterKeys { TechnicalIndicator.MOVING_AVERAGES.contains(it) })
                                val oscillatorSummary = calculateSummary(signalMap.data.filterKeys { TechnicalIndicator.OSCILLATORS.contains(it) })
                                val trendSummary = calculateSummary(signalMap.data.filterKeys { TechnicalIndicator.TRENDS.contains(it) })
                                val overallSummary = calculateSummary(signalMap.data)

                                val summaries = mapOf(
                                    IndicatorType.MOVING_AVERAGE to AnalysisSignalSummary.MovingAverageSummary(
                                        buy = movingAverageSummary.buy,
                                        sell = movingAverageSummary.sell,
                                        neutral = movingAverageSummary.neutral,
                                        summary = movingAverageSummary.summary
                                    ),
                                    IndicatorType.OSCILLATOR to AnalysisSignalSummary.OscillatorSummary(
                                        buy = oscillatorSummary.buy,
                                        sell = oscillatorSummary.sell,
                                        neutral = oscillatorSummary.neutral,
                                        summary = oscillatorSummary.summary
                                    ),
                                    IndicatorType.TREND to AnalysisSignalSummary.TrendSummary(
                                        buy = trendSummary.buy,
                                        sell = trendSummary.sell,
                                        neutral = trendSummary.neutral,
                                        summary = trendSummary.summary
                                    ),
                                    IndicatorType.ALL to AnalysisSignalSummary.OverallSummary(
                                        buy = overallSummary.buy,
                                        sell = overallSummary.sell,
                                        neutral = overallSummary.neutral,
                                        summary = overallSummary.summary
                                    )
                                )

                                signalSummaryMap[interval] = Result.Success(summaries)
                            }
                            else -> signalSummaryMap[interval] = Result.Error(DataError.Network.UNKNOWN)
                        }
                    }
                }.awaitAll()
            }
            emit(signalSummaryMap.toMap())
        }
    }.flowOn(ioDispatcher)

    /**
     * Calculates summary of the signals for a given map of indicators by taking the average of buy(1), sell(-1), or neutral(0).
     */
    private fun calculateSummary(indicatorSignalMap: Map<TechnicalIndicator, AnalysisSignal>): Quadruple<Int, Int, Int, Double> {
        val buyCount = indicatorSignalMap.values.count { it.signal == QuoteSignal.BUY }
        val sellCount = indicatorSignalMap.values.count { it.signal == QuoteSignal.SELL }
        val neutralCount = indicatorSignalMap.values.count { it.signal == QuoteSignal.NEUTRAL }
        val total = buyCount + sellCount + neutralCount
        val summary = (buyCount - sellCount) / total.toDouble()

        return Quadruple(buyCount, sellCount, neutralCount, summary)
    }

    private data class Quadruple<A, B, C, D>(val buy: A, val sell: B, val neutral: C, val summary: D)
}