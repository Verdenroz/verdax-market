package com.verdenroz.verdaxmarket.core.model.indicators

import com.verdenroz.verdaxmarket.core.model.enums.QuoteSignal

data class IchimokuCloud(
    val conversionLine: Double?,
    val baseLine: Double?,
    val laggingSpan: Double?,
    val leadingSpanA: Double?,
    val leadingSpanB: Double?
) : AnalysisIndicator.TrendIndicator {

    override fun toSignal(currentPrice: Double): QuoteSignal {
        // Return NEUTRAL if any required values are null
        if (conversionLine == null || baseLine == null ||
            leadingSpanA == null || leadingSpanB == null) {
            return QuoteSignal.NEUTRAL
        }

        // Determine if price is above or below the cloud
        val isAboveCloud = currentPrice > leadingSpanA && currentPrice > leadingSpanB
        val isBelowCloud = currentPrice < leadingSpanA && currentPrice < leadingSpanB

        return when {
            // Strong buy signal: Price above cloud + Conversion Line above Base Line
            isAboveCloud && conversionLine > baseLine &&
                    leadingSpanA > leadingSpanB -> QuoteSignal.BUY

            // Strong sell signal: Price below cloud + Conversion Line below Base Line
            isBelowCloud && conversionLine < baseLine &&
                    leadingSpanA < leadingSpanB -> QuoteSignal.SELL

            // Default to neutral for all other scenarios
            else -> QuoteSignal.NEUTRAL
        }
    }
}