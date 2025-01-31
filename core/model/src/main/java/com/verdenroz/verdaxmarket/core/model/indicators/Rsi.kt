package com.verdenroz.verdaxmarket.core.model.indicators

import com.verdenroz.verdaxmarket.core.model.enums.QuoteSignal

data class Rsi(
    val rsi: Double
) : AnalysisIndicator.OscillatorIndicator {

    override fun toSignal(): QuoteSignal {
        return when {
            rsi > 70 -> QuoteSignal.SELL
            rsi < 30 -> QuoteSignal.BUY
            else -> QuoteSignal.NEUTRAL
        }
    }
}