package com.verdenroz.verdaxmarket.core.data.model

import com.verdenroz.verdaxmarket.core.model.MarketMover
import com.verdenroz.verdaxmarket.core.network.model.ScreenersWrapperDto
import com.verdenroz.verdaxmarket.core.network.model.ScreenerQuoteDto
import java.text.DecimalFormat

/**
 * Helper function to format a Double price value to a String.
 */
private fun Double?.toFormattedPriceString(): String {
    if (this == null) return "0.00"
    val formatter = DecimalFormat("#,##0.00")
    return formatter.format(this)
}

/**
 * Helper function to format a Double percentage value to a String with % sign.
 */
private fun Double?.toFormattedPercentString(): String {
    if (this == null) return "0.00%"
    val formatter = DecimalFormat("#,##0.00")
    return "${formatter.format(this)}%"
}

/**
 * Maps v2 ScreenerQuoteDto to MarketMover domain model.
 */
fun ScreenerQuoteDto.asMarketMover() = MarketMover(
    symbol = symbol,
    name = name ?: longName ?: shortName ?: symbol,
    price = regularMarketPrice.toFormattedPriceString(),
    change = regularMarketChange.toFormattedPriceString(),
    percentChange = regularMarketChangePercent.toFormattedPercentString(),
)

/**
 * Maps v2 ScreenersWrapperDto to a list of MarketMover domain models.
 */
fun ScreenersWrapperDto.asExternalModel(): List<MarketMover> {
    return quotes.map { it.asMarketMover() }
}

/**
 * Maps a list of ScreenerQuoteDto to a list of MarketMover domain models.
 */
fun List<ScreenerQuoteDto>.asExternalModel(): List<MarketMover> {
    return map { it.asMarketMover() }
}