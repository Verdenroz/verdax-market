package com.verdenroz.verdaxmarket.core.model

/**
 * A data class that holds the counts of all signals and summarizes them.
 * @param buy the number of buy signals.
 * @param sell the number of sell signals.
 * @param neutral the number of neutral signals.
 */
sealed class AnalysisSignalSummary(
    open val buy: Int,
    open val sell: Int,
    open val neutral: Int,
) {
    data class MovingAverageSummary(
        override val buy: Int,
        override val sell: Int,
        override val neutral: Int,
    ) : AnalysisSignalSummary(buy, sell, neutral)

    data class OscillatorSummary(
        override val buy: Int,
        override val sell: Int,
        override val neutral: Int,
    ) : AnalysisSignalSummary(buy, sell, neutral)

    data class TrendSummary(
        override val buy: Int,
        override val sell: Int,
        override val neutral: Int,
    ) : AnalysisSignalSummary(buy, sell, neutral)

    data class OverallSummary(
        override val buy: Int,
        override val sell: Int,
        override val neutral: Int,
    ) : AnalysisSignalSummary(buy, sell, neutral)
}
