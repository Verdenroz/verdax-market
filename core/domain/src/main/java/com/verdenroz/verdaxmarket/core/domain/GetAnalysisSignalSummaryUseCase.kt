package com.verdenroz.verdaxmarket.core.domain

import com.verdenroz.verdaxmarket.core.common.dispatchers.Dispatcher
import com.verdenroz.verdaxmarket.core.common.dispatchers.FinanceQueryDispatchers
import com.verdenroz.verdaxmarket.core.common.enums.Interval
import com.verdenroz.verdaxmarket.core.common.error.DataError
import com.verdenroz.verdaxmarket.core.common.result.Result
import com.verdenroz.verdaxmarket.core.model.AnalysisSignal
import com.verdenroz.verdaxmarket.core.model.AnalysisSignalSummary
import com.verdenroz.verdaxmarket.core.model.enums.QuoteSignal
import com.verdenroz.verdaxmarket.core.model.indicators.IndicatorType
import com.verdenroz.verdaxmarket.core.model.indicators.TechnicalIndicator
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge
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
    ): Flow<Map<Interval, Result<Map<IndicatorType, AnalysisSignalSummary>, DataError.Network>>> = signals.flatMapMerge { analysisSignal ->
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
                                        buy = movingAverageSummary.first,
                                        sell = movingAverageSummary.second,
                                        neutral = movingAverageSummary.third,
                                    ),
                                    IndicatorType.OSCILLATOR to AnalysisSignalSummary.OscillatorSummary(
                                        buy = oscillatorSummary.first,
                                        sell = oscillatorSummary.second,
                                        neutral = oscillatorSummary.third,
                                    ),
                                    IndicatorType.TREND to AnalysisSignalSummary.TrendSummary(
                                        buy = trendSummary.first,
                                        sell = trendSummary.second,
                                        neutral = trendSummary.third,
                                    ),
                                    IndicatorType.ALL to AnalysisSignalSummary.OverallSummary(
                                        buy = overallSummary.first,
                                        sell = overallSummary.second,
                                        neutral = overallSummary.third,
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
    private fun calculateSummary(indicatorSignalMap: Map<TechnicalIndicator, AnalysisSignal>): Triple<Int, Int, Int> {
        val buyCount = indicatorSignalMap.values.count { it.signal == QuoteSignal.BUY }
        val sellCount = indicatorSignalMap.values.count { it.signal == QuoteSignal.SELL }
        val neutralCount = indicatorSignalMap.values.count { it.signal == QuoteSignal.NEUTRAL }

        return Triple(buyCount, sellCount, neutralCount)
    }

}