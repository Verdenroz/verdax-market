package com.verdenroz.verdaxmarket.core.model.indicators

import com.verdenroz.verdaxmarket.core.model.enums.QuoteSignal

data class Srsi(
    val k: Double?,    // %K line (faster)
    val d: Double?     // %D line (slower, signal line)
) : AnalysisIndicator.OscillatorIndicator {

    companion object {
        private const val OVERBOUGHT_THRESHOLD = 80.0
        private const val OVERSOLD_THRESHOLD = 20.0
        private const val EXTREME_OVERBOUGHT = 90.0
        private const val EXTREME_OVERSOLD = 10.0
        // Minimum difference between K and D to consider a valid crossover
        private const val MINIMUM_CROSSOVER_THRESHOLD = 0.1
    }

    override fun toSignal(): QuoteSignal {
        // Null safety checks
        val kValue = k ?: return QuoteSignal.NEUTRAL
        val dValue = d ?: return QuoteSignal.NEUTRAL

        // Calculate the difference between K and D lines
        val difference = kValue - dValue

        return when {
            // Extreme overbought conditions - Strong sell
            kValue >= EXTREME_OVERBOUGHT && dValue >= EXTREME_OVERBOUGHT -> QuoteSignal.SELL

            // Extreme oversold conditions - Strong buy
            kValue <= EXTREME_OVERSOLD && dValue <= EXTREME_OVERSOLD -> QuoteSignal.BUY

            // Overbought with bearish crossover (K crosses below D)
            kValue > OVERBOUGHT_THRESHOLD && dValue > OVERBOUGHT_THRESHOLD &&
                    difference < -MINIMUM_CROSSOVER_THRESHOLD -> QuoteSignal.SELL

            // Oversold with bullish crossover (K crosses above D)
            kValue < OVERSOLD_THRESHOLD && dValue < OVERSOLD_THRESHOLD &&
                    difference > MINIMUM_CROSSOVER_THRESHOLD -> QuoteSignal.BUY

            // Regular overbought condition
            kValue > OVERBOUGHT_THRESHOLD && dValue > OVERBOUGHT_THRESHOLD -> QuoteSignal.SELL

            // Regular oversold condition
            kValue < OVERSOLD_THRESHOLD && dValue < OVERSOLD_THRESHOLD -> QuoteSignal.BUY

            // No clear signal
            else -> QuoteSignal.NEUTRAL
        }
    }
}
