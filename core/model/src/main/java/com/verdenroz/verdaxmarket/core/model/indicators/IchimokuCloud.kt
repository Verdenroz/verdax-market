package com.verdenroz.verdaxmarket.core.model.indicators

import com.verdenroz.verdaxmarket.core.model.QuoteSignal

data class IchimokuCloud(
    val conversionLine: Double,
    val baseLine: Double,
    val laggingSpan: Double?,
    val leadingSpanA: Double?,
    val leadingSpanB: Double?
) : AnalysisIndicator.TrendIndicator {

    override fun toSignal(currentPrice: Double): QuoteSignal {
        return when {
            leadingSpanA != null && leadingSpanB != null && leadingSpanA > leadingSpanB -> QuoteSignal.BUY
            leadingSpanA != null && leadingSpanB != null && leadingSpanA < leadingSpanB -> QuoteSignal.SELL
            else -> QuoteSignal.NEUTRAL
        }
    }
}