package com.verdenroz.verdaxmarket.core.model

/**
 * A data class that holds the counts of all signals and summarizes them.
 * @param buy the number of buy signals.
 * @param sell the number of sell signals.
 * @param neutral the number of neutral signals.
 * @param summary the summary of all the signals as the average of buy(1), sell(-1), or neutral(0).
 */
sealed class AnalysisSignalSummary(
    open val buy: Int,
    open val sell: Int,
    open val neutral: Int,
    open val summary: Double
) {
    data class MovingAverageSummary(
        override val buy: Int,
        override val sell: Int,
        override val neutral: Int,
        override val summary: Double
    ) : AnalysisSignalSummary(buy, sell, neutral, summary)

    data class OscillatorSummary(
        override val buy: Int,
        override val sell: Int,
        override val neutral: Int,
        override val summary: Double
    ) : AnalysisSignalSummary(buy, sell, neutral, summary)

    data class TrendSummary(
        override val buy: Int,
        override val sell: Int,
        override val neutral: Int,
        override val summary: Double
    ) : AnalysisSignalSummary(buy, sell, neutral, summary)

    data class OverallSummary(
        override val buy: Int,
        override val sell: Int,
        override val neutral: Int,
        override val summary: Double
    ) : AnalysisSignalSummary(buy, sell, neutral, summary)
}
