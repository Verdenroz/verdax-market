package com.verdenroz.verdaxmarket.core.model.indicators

import com.verdenroz.verdaxmarket.core.model.enums.QuoteSignal

data class Adx(
    val adx: Double?,
) : AnalysisIndicator.TrendIndicator {

    override fun toSignal(currentPrice: Double): QuoteSignal = QuoteSignal.NEUTRAL
}
