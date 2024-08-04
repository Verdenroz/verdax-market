package com.verdenroz.verdaxmarket.domain

import com.verdenroz.verdaxmarket.core.model.AnalysisSignal
import com.verdenroz.verdaxmarket.core.model.QuoteSignal
import com.verdenroz.verdaxmarket.core.model.indicators.TechnicalIndicator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Use case that calculates the summary of the signals for a given set of indicators
 */
class GetAnalysisSummaryUseCase @Inject constructor() {

    operator fun invoke(
        signalMap: Flow<Map<TechnicalIndicator, AnalysisSignal>>,
        indicators: Set<TechnicalIndicator>
    ): Flow<Double> = signalMap.map { signals ->

        val filteredSignals = signals.filterKeys { it in indicators }
        val value = { signal: QuoteSignal ->
            when (signal) {
                QuoteSignal.BUY -> 1
                QuoteSignal.SELL -> -1
                QuoteSignal.NEUTRAL -> 0
            }
        }
        val sum = filteredSignals.values.sumOf { value(it.signal) }
        if (filteredSignals.isNotEmpty()) sum.toDouble() / filteredSignals.size else 0.0
    }
}