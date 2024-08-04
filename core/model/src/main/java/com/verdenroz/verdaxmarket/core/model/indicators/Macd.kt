package com.verdenroz.verdaxmarket.core.model.indicators

import com.verdenroz.verdaxmarket.core.model.QuoteSignal

data class Macd(
    val macd: Double,
    val signal: Double,
) : AnalysisIndicator.TrendIndicator {

    override fun toSignal(currentPrice: Double): QuoteSignal {
        return when {
            macd > signal -> QuoteSignal.BUY
            macd < signal -> QuoteSignal.SELL
            else -> QuoteSignal.NEUTRAL
        }
    }
}
