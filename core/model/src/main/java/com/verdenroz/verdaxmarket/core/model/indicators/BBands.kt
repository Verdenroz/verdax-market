package com.verdenroz.verdaxmarket.core.model.indicators

import com.verdenroz.verdaxmarket.core.model.enums.QuoteSignal

data class BBands(
    val upperBand: Double?,
    val middleBand: Double?,
    val lowerBand: Double?
) : AnalysisIndicator.TrendIndicator {

    companion object {
        // Threshold for determining strong signals (percentage of band width)
        private const val BAND_PENETRATION_THRESHOLD = 0.05 // 5% penetration threshold
    }

    override fun toSignal(currentPrice: Double): QuoteSignal {
        // Null safety checks
        val upper = upperBand ?: return QuoteSignal.NEUTRAL
        val middle = middleBand ?: return QuoteSignal.NEUTRAL
        val lower = lowerBand ?: return QuoteSignal.NEUTRAL

        // Calculate band width and penetration thresholds
        val bandWidth = upper - lower
        val penetrationDistance = bandWidth * BAND_PENETRATION_THRESHOLD

        return when {
            // Strong sell signals:
            // 1. Price is above upper band by penetration threshold
            // 2. Price is above upper band and above moving average significantly
            currentPrice > (upper + penetrationDistance) ||
                    (currentPrice > upper && currentPrice > (middle * 1.02)) -> QuoteSignal.SELL

            // Strong buy signals:
            // 1. Price is below lower band by penetration threshold
            // 2. Price is below lower band and below moving average significantly
            currentPrice < (lower - penetrationDistance) ||
                    (currentPrice < lower && currentPrice < (middle * 0.98)) -> QuoteSignal.BUY

            // Price is within the bands - neutral signal
            else -> QuoteSignal.NEUTRAL
        }
    }

    /**
     * Calculate the percentage bandwidth (volatility indicator)
     * Returns null if any of the bands are null
     */
    fun getBandwidthPercentage(): Double? {
        if (upperBand == null || middleBand == null || lowerBand == null) {
            return null
        }
        return ((upperBand - lowerBand) / middleBand) * 100
    }

    /**
     * Calculate the relative position of price within the bands (0-100%)
     * Returns null if any of the bands are null
     */
    fun getPercentB(currentPrice: Double): Double? {
        if (upperBand == null || lowerBand == null) {
            return null
        }
        val bandwidth = upperBand - lowerBand
        if (bandwidth == 0.0) return 50.0 // To avoid division by zero
        return ((currentPrice - lowerBand) / bandwidth) * 100
    }
}