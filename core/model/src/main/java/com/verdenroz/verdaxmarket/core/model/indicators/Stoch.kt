package com.verdenroz.verdaxmarket.core.model.indicators

import com.verdenroz.verdaxmarket.core.model.enums.QuoteSignal

data class Stoch(
    val stoch: Double
) : AnalysisIndicator.OscillatorIndicator {

    override fun toSignal(): QuoteSignal {
        return when {
            stoch > 80 -> QuoteSignal.SELL
            stoch < 20 -> QuoteSignal.BUY
            else -> QuoteSignal.NEUTRAL
        }
    }
}
