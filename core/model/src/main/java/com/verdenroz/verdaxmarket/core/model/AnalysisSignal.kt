package com.verdenroz.verdaxmarket.core.model

import com.verdenroz.verdaxmarket.core.model.enums.QuoteSignal
import com.verdenroz.verdaxmarket.core.model.indicators.AnalysisIndicator

/**
 * Represents a signal generated by an indicator combining [AnalysisIndicator] and [com.verdenroz.verdaxmarket.core.model.enums.QuoteSignal]
 */
sealed class AnalysisSignal(
    open val indicator: AnalysisIndicator,
    open val signal: QuoteSignal
) {
    data class MovingAverageSignal(
        override val indicator: AnalysisIndicator.MovingAverageIndicator,
        override val signal: QuoteSignal
    ) : AnalysisSignal(indicator, signal)

    data class OscillatorSignal(
        override val indicator: AnalysisIndicator.OscillatorIndicator,
        override val signal: QuoteSignal
    ) : AnalysisSignal(indicator, signal)

    data class TrendSignal(
        override val indicator: AnalysisIndicator.TrendIndicator,
        override val signal: QuoteSignal
    ) : AnalysisSignal(indicator, signal)
}
