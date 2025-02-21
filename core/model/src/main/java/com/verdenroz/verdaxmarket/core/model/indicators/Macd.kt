package com.verdenroz.verdaxmarket.core.model.indicators

import com.verdenroz.verdaxmarket.core.model.enums.QuoteSignal
import kotlin.math.abs

data class Macd(
    val macd: Double?,
    val signal: Double?,
) : AnalysisIndicator.TrendIndicator {

    companion object {
        // Minimum difference between MACD and Signal to generate a signal
        // This helps filter out noise and weak signals
        private const val MINIMUM_DIFFERENCE_THRESHOLD = 0.001
    }

    override fun toSignal(currentPrice: Double): QuoteSignal {
        // Null safety checks
        val currentMacd = macd ?: return QuoteSignal.NEUTRAL
        val currentSignal = signal ?: return QuoteSignal.NEUTRAL

        // Calculate the difference between MACD and Signal
        val difference = currentMacd - currentSignal

        return when {
            // If the difference is too small, return NEUTRAL to avoid noise
            abs(difference) < MINIMUM_DIFFERENCE_THRESHOLD -> QuoteSignal.NEUTRAL

            // BUY signal when MACD is above Signal
            difference > 0 -> QuoteSignal.BUY

            // SELL signal when MACD is below Signal
            difference < 0 -> QuoteSignal.SELL

            // Default case (should rarely occur due to above conditions)
            else -> QuoteSignal.NEUTRAL
        }
    }

}
