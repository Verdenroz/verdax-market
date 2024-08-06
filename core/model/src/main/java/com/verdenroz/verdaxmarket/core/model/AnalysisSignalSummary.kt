package com.verdenroz.verdaxmarket.core.model

/**
 * A data class that represents the summary of the analysis signals as the average of buy(1), sell(-1), or neutral(0).
 * @param movingAverageSummary - the summary of the moving average signals.
 * @param oscillatorSummary - the summary of the oscillator signals.
 * @param trendSummary - the summary of the trend signals.
 * @param summary - the summary of all the signals.
 */
data class AnalysisSignalSummary(
    val movingAverageSummary: Double,
    val oscillatorSummary: Double,
    val trendSummary: Double,
    val summary: Double,
)
