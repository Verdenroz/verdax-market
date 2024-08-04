package com.verdenroz.verdaxmarket.core.model.indicators

import com.verdenroz.verdaxmarket.core.model.QuoteSignal

data class Srsi(
    val srsi: Double
) : AnalysisIndicator.OscillatorIndicator {

    override fun toSignal(): QuoteSignal {
        return when {
            srsi > 80 -> QuoteSignal.SELL
            srsi < 20 -> QuoteSignal.BUY
            else -> QuoteSignal.NEUTRAL
        }
    }
}
