package com.verdenroz.verdaxmarket.core.network.model

import com.verdenroz.verdaxmarket.core.network.NullableDoubleSerializer
import com.verdenroz.verdaxmarket.core.network.NullableLongSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

/**
 * DTO for a single quote from the v2 API.
 * This is used for both /v2/quote/{symbol} and individual quotes in batch responses.
 *
 * Note: The API returns empty objects {} for unavailable numeric values,
 * so we use custom serializers to handle these cases.
 *
 * @param symbol The stock symbol
 * @param name The stock name
 * @param regularMarketPrice The current regular market price
 * @param regularMarketChange The change in regular market price
 * @param regularMarketChangePercent The percentage change in regular market price
 * @param regularMarketOpen The opening price of the regular market session
 * @param regularMarketDayHigh The highest price during the regular market session
 * @param regularMarketDayLow The lowest price during the regular market session
 * @param regularMarketVolume The volume during the regular market session
 * @param regularMarketPreviousClose The previous close price
 * @param preMarketPrice The pre-market price
 * @param preMarketChange The change in pre-market price
 * @param preMarketChangePercent The percentage change in pre-market price
 * @param postMarketPrice The post-market price
 * @param postMarketChange The change in post-market price
 * @param postMarketChangePercent The percentage change in post-market price
 * @param fiftyTwoWeekHigh The 52-week high price
 * @param fiftyTwoWeekLow The 52-week low price
 * @param averageDailyVolume3Month The average daily volume over 3 months
 * @param averageDailyVolume10Day The average daily volume over 10 days
 * @param marketCap The market capitalization
 * @param beta The beta value
 * @param trailingPE The trailing P/E ratio
 * @param forwardPE The forward P/E ratio
 * @param trailingEps The trailing EPS
 * @param dividendRate The annual dividend rate
 * @param dividendYield The dividend yield
 * @param exDividendDate The ex-dividend date (Unix timestamp)
 * @param earningsTimestamp The earnings announcement timestamp
 * @param sector The sector the stock belongs to
 * @param industry The industry the stock belongs to
 * @param quoteType The type of quote (e.g., EQUITY, ETF, MUTUALFUND)
 * @param currency The currency of the quote
 * @param exchange The exchange where the stock is traded
 * @param shortName The short name of the stock
 * @param longName The long name of the stock
 * @param logo The URL of the company logo (mapped from logoUrl or companyLogoUrl, only with logo=true)
 * @param fiveDayReturn The 5-day return percentage
 * @param oneMonthReturn The 1-month return percentage
 * @param sixMonthReturn The 6-month return percentage
 * @param ytdReturn The year-to-date return percentage
 * @param oneYearReturn The 1-year return percentage
 * @param fiveYearReturn The 5-year return percentage
 */
@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class QuoteDto(
    val symbol: String,
    val name: String? = null,
    @Serializable(with = NullableDoubleSerializer::class)
    val regularMarketPrice: Double? = null,
    @Serializable(with = NullableDoubleSerializer::class)
    val regularMarketChange: Double? = null,
    @Serializable(with = NullableDoubleSerializer::class)
    val regularMarketChangePercent: Double? = null,
    @Serializable(with = NullableDoubleSerializer::class)
    val regularMarketOpen: Double? = null,
    @Serializable(with = NullableDoubleSerializer::class)
    val regularMarketDayHigh: Double? = null,
    @Serializable(with = NullableDoubleSerializer::class)
    val regularMarketDayLow: Double? = null,
    @Serializable(with = NullableLongSerializer::class)
    val regularMarketVolume: Long? = null,
    @Serializable(with = NullableDoubleSerializer::class)
    val regularMarketPreviousClose: Double? = null,
    @Serializable(with = NullableDoubleSerializer::class)
    val preMarketPrice: Double? = null,
    @Serializable(with = NullableDoubleSerializer::class)
    val preMarketChange: Double? = null,
    @Serializable(with = NullableDoubleSerializer::class)
    val preMarketChangePercent: Double? = null,
    @Serializable(with = NullableDoubleSerializer::class)
    val postMarketPrice: Double? = null,
    @Serializable(with = NullableDoubleSerializer::class)
    val postMarketChange: Double? = null,
    @Serializable(with = NullableDoubleSerializer::class)
    val postMarketChangePercent: Double? = null,
    @Serializable(with = NullableDoubleSerializer::class)
    val fiftyTwoWeekHigh: Double? = null,
    @Serializable(with = NullableDoubleSerializer::class)
    val fiftyTwoWeekLow: Double? = null,
    @Serializable(with = NullableLongSerializer::class)
    val averageDailyVolume3Month: Long? = null,
    @Serializable(with = NullableLongSerializer::class)
    val averageDailyVolume10Day: Long? = null,
    @Serializable(with = NullableLongSerializer::class)
    val marketCap: Long? = null,
    @Serializable(with = NullableDoubleSerializer::class)
    val beta: Double? = null,
    @Serializable(with = NullableDoubleSerializer::class)
    val trailingPE: Double? = null,
    @Serializable(with = NullableDoubleSerializer::class)
    val forwardPE: Double? = null,
    @Serializable(with = NullableDoubleSerializer::class)
    val trailingEps: Double? = null,
    @Serializable(with = NullableDoubleSerializer::class)
    val dividendRate: Double? = null,
    @Serializable(with = NullableDoubleSerializer::class)
    val dividendYield: Double? = null,
    @Serializable(with = NullableLongSerializer::class)
    val exDividendDate: Long? = null,
    @Serializable(with = NullableLongSerializer::class)
    val earningsTimestamp: Long? = null,
    val sector: String? = null,
    val industry: String? = null,
    val quoteType: String? = null,
    val currency: String? = null,
    val exchange: String? = null,
    val shortName: String? = null,
    val longName: String? = null,
    @JsonNames("logoUrl", "companyLogoUrl")
    val logo: String? = null,
    @Serializable(with = NullableDoubleSerializer::class)
    val fiveDayReturn: Double? = null,
    @Serializable(with = NullableDoubleSerializer::class)
    val oneMonthReturn: Double? = null,
    @Serializable(with = NullableDoubleSerializer::class)
    val sixMonthReturn: Double? = null,
    @Serializable(with = NullableDoubleSerializer::class)
    val ytdReturn: Double? = null,
    @Serializable(with = NullableDoubleSerializer::class)
    val oneYearReturn: Double? = null,
    @Serializable(with = NullableDoubleSerializer::class)
    val fiveYearReturn: Double? = null,
)

/**
 * DTO for batch quote requests from the v2 API.
 * Used by /v2/quotes?symbols={comma-separated-list}
 *
 * @param quotes A map of symbol to QuoteDto for successful quote retrievals
 * @param errors A map of symbol to error message for failed quote retrievals
 */
@Serializable
data class BatchQuotesDto(
    val quotes: Map<String, QuoteDto> = emptyMap(),
    val errors: Map<String, String> = emptyMap(),
)
