package com.verdenroz.verdaxmarket.core.model.indicators

import com.verdenroz.verdaxmarket.core.model.enums.QuoteSignal

data class Cci(
    val cci: Double
) : AnalysisIndicator.OscillatorIndicator {

    override fun toSignal(): QuoteSignal {
        return when {
            cci < -100 -> QuoteSignal.BUY
            cci > 100 -> QuoteSignal.SELL
            else -> QuoteSignal.NEUTRAL
        }
    }
}