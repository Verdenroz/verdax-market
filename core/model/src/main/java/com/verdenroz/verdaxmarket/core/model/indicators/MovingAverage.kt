package com.verdenroz.verdaxmarket.core.model.indicators

import com.verdenroz.verdaxmarket.core.model.enums.QuoteSignal

data class MovingAverage(
    val value: Double?
) : AnalysisIndicator.MovingAverageIndicator {

    override fun toSignal(currentPrice: Double): QuoteSignal {
        val value = value ?: return QuoteSignal.NEUTRAL

        return when {
            value < currentPrice -> QuoteSignal.BUY
            value > currentPrice -> QuoteSignal.SELL
            else -> QuoteSignal.NEUTRAL
        }
    }
}