package com.verdenroz.verdaxmarket.core.model.indicators

import com.verdenroz.verdaxmarket.core.model.enums.QuoteSignal

data class Aroon(
    val aroonUp: Double?,    // Aroon Up (tracks days since high)
    val aroonDown: Double?   // Aroon Down (tracks days since low)
) : AnalysisIndicator.TrendIndicator {

    companion object {
        // Threshold for strong trend signals (70+ is considered strong)
        private const val STRONG_TREND_THRESHOLD = 70.0
        // Threshold for weak signals (30 or below is considered weak)
        private const val WEAK_TREND_THRESHOLD = 30.0
        // Minimum difference between Up and Down for valid signals
        private const val MINIMUM_DIFFERENCE_THRESHOLD = 10.0
    }

    override fun toSignal(currentPrice: Double): QuoteSignal {
        // Null safety checks
        val up = aroonUp ?: return QuoteSignal.NEUTRAL
        val down = aroonDown ?: return QuoteSignal.NEUTRAL

        // Calculate the difference between Up and Down
        val difference = up - down

        return when {
            // Strong uptrend signals
            up > STRONG_TREND_THRESHOLD &&
                    down < WEAK_TREND_THRESHOLD &&
                    difference > MINIMUM_DIFFERENCE_THRESHOLD -> QuoteSignal.BUY

            // Strong downtrend signals
            down > STRONG_TREND_THRESHOLD &&
                    up < WEAK_TREND_THRESHOLD &&
                    difference < -MINIMUM_DIFFERENCE_THRESHOLD -> QuoteSignal.SELL

            // Weaker but valid uptrend
            up > down && difference > MINIMUM_DIFFERENCE_THRESHOLD -> QuoteSignal.BUY

            // Weaker but valid downtrend
            down > up && difference < -MINIMUM_DIFFERENCE_THRESHOLD -> QuoteSignal.SELL

            // No clear trend
            else -> QuoteSignal.NEUTRAL
        }
    }
}