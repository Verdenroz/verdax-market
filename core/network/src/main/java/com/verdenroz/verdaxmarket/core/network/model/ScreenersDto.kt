package com.verdenroz.verdaxmarket.core.network.model

import com.verdenroz.verdaxmarket.core.network.NullableDoubleSerializer
import com.verdenroz.verdaxmarket.core.network.NullableLongSerializer
import kotlinx.serialization.Serializable

/**
 * Wrapper DTO for screener responses from the v2 API.
 * Used by /v2/screeners/most_actives, /v2/screeners/day_gainers, /v2/screeners/day_losers
 *
 * @param quotes List of quotes matching the screener criteria
 * @param type The type of screener (e.g., "most_actives", "day_gainers", "day_losers")
 * @param description Description of the screener
 * @param lastUpdated Timestamp when the screener was last updated
 */
@Serializable
data class ScreenersWrapperDto(
    val quotes: List<ScreenerQuoteDto> = emptyList(),
    val type: String? = null,
    val description: String? = null,
    @Serializable(with = NullableLongSerializer::class)
    val lastUpdated: Long? = null,
)

/**
 * DTO for a single quote in a screener response.
 * Note: When using ?format=raw parameter, all numeric fields are returned as simple numbers
 * instead of FormattedValue objects.
 *
 * @param symbol The stock symbol
 * @param name The stock name
 * @param regularMarketPrice The current regular market price
 * @param regularMarketChange The change in regular market price
 * @param regularMarketChangePercent The percentage change in regular market price
 * @param regularMarketVolume The volume during the regular market session
 * @param regularMarketDayHigh The highest price during the regular market session
 * @param regularMarketDayLow The lowest price during the regular market session
 * @param regularMarketOpen The opening price of the regular market session
 * @param regularMarketPreviousClose The previous close price
 * @param marketCap The market capitalization
 * @param averageDailyVolume3Month The average daily volume over 3 months
 * @param fiftyTwoWeekHigh The 52-week high price
 * @param fiftyTwoWeekLow The 52-week low price
 * @param trailingPE The trailing P/E ratio
 * @param quoteType The type of quote (e.g., "EQUITY", "ETF")
 * @param exchange The exchange where the stock is traded
 * @param shortName The short name of the stock
 * @param longName The long name of the stock
 */
@Serializable
data class ScreenerQuoteDto(
    val symbol: String,
    val name: String? = null,
    @Serializable(with = NullableDoubleSerializer::class)
    val regularMarketPrice: Double? = null,
    @Serializable(with = NullableDoubleSerializer::class)
    val regularMarketChange: Double? = null,
    @Serializable(with = NullableDoubleSerializer::class)
    val regularMarketChangePercent: Double? = null,
    @Serializable(with = NullableLongSerializer::class)
    val regularMarketVolume: Long? = null,
    @Serializable(with = NullableDoubleSerializer::class)
    val regularMarketDayHigh: Double? = null,
    @Serializable(with = NullableDoubleSerializer::class)
    val regularMarketDayLow: Double? = null,
    @Serializable(with = NullableDoubleSerializer::class)
    val regularMarketOpen: Double? = null,
    @Serializable(with = NullableDoubleSerializer::class)
    val regularMarketPreviousClose: Double? = null,
    @Serializable(with = NullableLongSerializer::class)
    val marketCap: Long? = null,
    @Serializable(with = NullableLongSerializer::class)
    val averageDailyVolume3Month: Long? = null,
    @Serializable(with = NullableDoubleSerializer::class)
    val fiftyTwoWeekHigh: Double? = null,
    @Serializable(with = NullableDoubleSerializer::class)
    val fiftyTwoWeekLow: Double? = null,
    @Serializable(with = NullableDoubleSerializer::class)
    val trailingPE: Double? = null,
    val quoteType: String? = null,
    val exchange: String? = null,
    val shortName: String? = null,
    val longName: String? = null,
)
