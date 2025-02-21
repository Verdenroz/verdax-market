package com.verdenroz.verdaxmarket.core.model.indicators

import com.verdenroz.verdaxmarket.core.model.enums.QuoteSignal

data class Stoch(
    val k: Double?,    // %K line (faster)
    val d: Double?     // %D line (slower, signal line)
) : AnalysisIndicator.OscillatorIndicator {

    companion object {
        private const val OVERBOUGHT_THRESHOLD = 80.0
        private const val OVERSOLD_THRESHOLD = 20.0
        // Minimum difference between K and D to consider a valid crossover
        private const val MINIMUM_CROSSOVER_THRESHOLD = 0.5
    }

    override fun toSignal(): QuoteSignal {
        // Null safety checks
        val kValue = k ?: return QuoteSignal.NEUTRAL
        val dValue = d ?: return QuoteSignal.NEUTRAL

        // Calculate the difference between K and D lines
        val difference = kValue - dValue

        return when {
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
