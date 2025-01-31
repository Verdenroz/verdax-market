package com.verdenroz.verdaxmarket.core.model.indicators

import com.verdenroz.verdaxmarket.core.model.enums.QuoteSignal

data class Aroon(
    val aroonUp: Double,
    val aroonDown: Double
) : AnalysisIndicator.TrendIndicator {

    override fun toSignal(currentPrice: Double): QuoteSignal {
        return when {
            aroonUp > aroonDown -> QuoteSignal.BUY
            aroonUp < aroonDown -> QuoteSignal.SELL
            else -> QuoteSignal.NEUTRAL
        }
    }
}