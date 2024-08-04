package com.verdenroz.verdaxmarket.core.model.indicators

import com.verdenroz.verdaxmarket.core.model.QuoteSignal

data class MovingAverage(
    val value: Double
) : AnalysisIndicator.MovingAverageIndicator {

    override fun toSignal(currentPrice: Double): QuoteSignal {
        return when {
            value < currentPrice -> QuoteSignal.BUY
            value > currentPrice -> QuoteSignal.SELL
            else -> QuoteSignal.NEUTRAL
        }
    }
}