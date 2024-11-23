package com.verdenroz.verdaxmarket.core.model.indicators

import com.verdenroz.verdaxmarket.core.model.QuoteSignal

data class BBands(
    val upperBand: Double,
    val lowerBand: Double
) : AnalysisIndicator.TrendIndicator {

    override fun toSignal(currentPrice: Double): QuoteSignal {
        return when {
            currentPrice > upperBand -> QuoteSignal.SELL
            currentPrice < lowerBand -> QuoteSignal.BUY
            else -> QuoteSignal.NEUTRAL
        }
    }
}