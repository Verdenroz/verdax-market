package com.verdenroz.verdaxmarket.core.data.model

import com.verdenroz.verdaxmarket.core.database.model.QuoteEntity
import com.verdenroz.verdaxmarket.core.model.FullQuoteData
import com.verdenroz.verdaxmarket.core.model.SimpleQuoteData
import com.verdenroz.verdaxmarket.core.model.WatchlistQuote
import com.verdenroz.verdaxmarket.core.network.model.QuoteDto
import com.verdenroz.verdaxmarket.core.network.model.BatchQuotesDto
import java.text.DecimalFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun QuoteEntity.asExternalModel() = WatchlistQuote(
    symbol = symbol,
    name = name,
    price = price,
    change = change,
    percentChange = percentChange,
    logo = logo,
    order = order,
)

/**
 * Helper function to format a Double price value to a String.
 * Returns formatted price with 2 decimal places, or null if input is null.
 */
private fun Double?.toFormattedPrice(): String? {
    if (this == null) return null
    val formatter = DecimalFormat("#,##0.00")
    return formatter.format(this)
}

/**
 * Helper function to format a Double percentage value to a String with % sign.
 * Returns formatted percentage with 2 decimal places and % sign, or null if input is null.
 */
private fun Double?.toFormattedPercentage(): String? {
    if (this == null) return null
    val formatter = DecimalFormat("#,##0.00")
    return "${formatter.format(this)}%"
}

/**
 * Helper function to format a Long market cap value to a String with suffix.
 */
private fun Long?.toFormattedMarketCap(): String? {
    if (this == null) return null
    return when {
        this >= 1_000_000_000_000 -> {
            val trillions = this / 1_000_000_000_000.0
            String.format(java.util.Locale.US, "%.2fT", trillions)
        }
        this >= 1_000_000_000 -> {
            val billions = this / 1_000_000_000.0
            String.format(java.util.Locale.US, "%.2fB", billions)
        }
        this >= 1_000_000 -> {
            val millions = this / 1_000_000.0
            String.format(java.util.Locale.US, "%.2fM", millions)
        }
        else -> this.toString()
    }
}

/**
 * Helper function to format a Unix timestamp to a date string.
 */
private fun Long?.toFormattedDate(): String? {
    if (this == null) return null
    return try {
        val instant = Instant.ofEpochSecond(this)
        val formatter = DateTimeFormatter.ofPattern("MMM d, yyyy")
            .withZone(ZoneId.systemDefault())
        formatter.format(instant)
    } catch (_: Exception) {
        null
    }
}

/**
 * Maps v2 QuoteDto to FullQuoteData domain model.
 */
fun QuoteDto.asExternalModel() = FullQuoteData(
    symbol = symbol,
    name = name ?: longName ?: shortName ?: symbol,
    price = regularMarketPrice.toFormattedPrice() ?: "0.00",
    preMarketPrice = preMarketPrice.toFormattedPrice(),
    afterHoursPrice = postMarketPrice.toFormattedPrice(),
    change = regularMarketChange.toFormattedPrice() ?: "0.00",
    percentChange = regularMarketChangePercent.toFormattedPercentage() ?: "0.00%",
    open = regularMarketOpen.toFormattedPrice(),
    high = regularMarketDayHigh.toFormattedPrice(),
    low = regularMarketDayLow.toFormattedPrice(),
    yearHigh = fiftyTwoWeekHigh.toFormattedPrice(),
    yearLow = fiftyTwoWeekLow.toFormattedPrice(),
    volume = regularMarketVolume,
    avgVolume = averageDailyVolume3Month,
    marketCap = marketCap.toFormattedMarketCap(),
    beta = beta.toFormattedPrice(),
    pe = trailingPE.toFormattedPrice(),
    eps = trailingEps.toFormattedPrice(),
    dividend = dividendRate.toFormattedPrice(),
    yield = dividendYield.toFormattedPercentage(),
    netAssets = null, // Not available in v2 basic quote
    nav = null, // Not available in v2 basic quote
    expenseRatio = null, // Not available in v2 basic quote
    category = null, // Not available in v2 basic quote
    lastCapitalGain = null, // Not available in v2 basic quote
    morningstarRating = null, // Not available in v2 basic quote
    morningstarRisk = null, // Not available in v2 basic quote
    holdingsTurnover = null, // Not available in v2 basic quote
    lastDividend = null, // Not available in v2 basic quote
    inceptionDate = null, // Not available in v2 basic quote
    exDividend = exDividendDate.toFormattedDate(),
    earningsDate = earningsTimestamp.toFormattedDate(),
    sector = sector,
    industry = industry,
    about = null, // Not available in v2 basic quote
    ytdReturn = null, // Not available in v2 basic quote
    yearReturn = null, // Not available in v2 basic quote
    threeYearReturn = null, // Not available in v2 basic quote
    fiveYearReturn = null, // Not available in v2 basic quote
    logo = logo,
    employees = null, // Not available in v2 basic quote
)

/**
 * Maps v2 QuoteDto to SimpleQuoteData domain model.
 */
fun QuoteDto.asSimpleQuoteData() = SimpleQuoteData(
    symbol = symbol,
    name = name ?: longName ?: shortName ?: symbol,
    price = regularMarketPrice.toFormattedPrice() ?: "0.00",
    change = regularMarketChange.toFormattedPrice() ?: "0.00",
    percentChange = regularMarketChangePercent.toFormattedPercentage() ?: "0.00%",
    logo = logo,
)

/**
 * Maps a list of QuoteDto to a list of SimpleQuoteData domain models.
 */
fun List<QuoteDto>.asExternalModel(): List<SimpleQuoteData> {
    return map { it.asSimpleQuoteData() }
}

/**
 * Maps v2 BatchQuotesDto to a list of SimpleQuoteData domain models.
 * Only successful quotes are included; errors are logged but not returned.
 */
fun BatchQuotesDto.asExternalModel(): List<SimpleQuoteData> {
    // Log errors if any (optional - could be handled at repository layer)
    if (errors.isNotEmpty()) {
        println("BatchQuotesDto errors: $errors")
    }
    return quotes.values.map { it.asSimpleQuoteData() }
}
