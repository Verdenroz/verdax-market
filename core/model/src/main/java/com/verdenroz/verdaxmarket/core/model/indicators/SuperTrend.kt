package com.verdenroz.verdaxmarket.core.model.indicators

import com.verdenroz.verdaxmarket.core.model.enums.QuoteSignal

data class SuperTrend(
    val superTrend: Double,
    val trend: String
): AnalysisIndicator.TrendIndicator {

    override fun toSignal(currentPrice: Double): QuoteSignal {
        return when {
            currentPrice > superTrend -> QuoteSignal.BUY
            currentPrice < superTrend -> QuoteSignal.SELL
            else -> QuoteSignal.NEUTRAL
        }
    }
}