package com.verdenroz.verdaxmarket.core.model.indicators

import com.verdenroz.verdaxmarket.core.model.enums.QuoteSignal

/**
 * Represents the trend direction for SuperTrend indicator
 */
private enum class TrendDirection {
    UP, DOWN;

    companion object {
        fun fromString(value: String?): TrendDirection? = when(value?.uppercase()) {
            "UP" -> UP
            "DOWN" -> DOWN
            else -> null
        }
    }
}

data class SuperTrend(
    val superTrend: Double?,
    private val trend: String?   // Trend direction as string ("UP" or "DOWN")
) : AnalysisIndicator.TrendIndicator {

    // Convert string trend to enum for type safety
    private val trendDirection: TrendDirection? = TrendDirection.fromString(trend)

    override fun toSignal(currentPrice: Double): QuoteSignal {
        // Null safety checks
        val superTrendValue = superTrend ?: return QuoteSignal.NEUTRAL
        val direction = trendDirection ?: return QuoteSignal.NEUTRAL

        return when {
            // Strong BUY: Uptrend and price well above SuperTrend
            direction == TrendDirection.UP &&
                    currentPrice > superTrendValue -> QuoteSignal.BUY

            // Strong SELL: Downtrend and price well below SuperTrend
            direction == TrendDirection.DOWN &&
                    currentPrice < superTrendValue -> QuoteSignal.SELL

            // Default to NEUTRAL if conditions aren't clear
            else -> QuoteSignal.NEUTRAL
        }
    }
}