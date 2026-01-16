package com.verdenroz.verdaxmarket.core.network.model

import com.verdenroz.verdaxmarket.core.network.NullableDoubleSerializer
import com.verdenroz.verdaxmarket.core.network.NullableLongSerializer
import kotlinx.serialization.Serializable

/**
 * DTO for chart/historical data from the v2 API.
 * Used by /v2/chart/{symbol}?range={range}&interval={interval}
 *
 * @param symbol The stock symbol
 * @param meta Metadata about the chart
 * @param candles Array of OHLCV candle data
 * @param interval The interval of the data (e.g., "1d", "1h", "5m")
 * @param range The range of the data (e.g., "1d", "5d", "1mo", "1y")
 */
@Serializable
data class ChartDto(
    val symbol: String,
    val meta: ChartMetaDto? = null,
    val candles: List<CandleDto> = emptyList(),
    val interval: String? = null,
    val range: String? = null,
)

/**
 * DTO for chart metadata from the v2 API.
 *
 * @param currency The currency of the chart data
 * @param symbol The stock symbol
 * @param exchangeName The name of the exchange
 * @param fullExchangeName The full name of the exchange
 * @param instrumentType The type of instrument (e.g., "EQUITY", "ETF")
 * @param firstTradeDate The first trade date timestamp
 * @param regularMarketTime The regular market time timestamp
 * @param regularMarketPrice The current regular market price
 * @param chartPreviousClose The previous close price for the chart
 * @param priceHint The number of decimal places for prices
 * @param currentTradingPeriod Information about current trading period
 * @param dataGranularity The granularity of the data
 * @param range The range of the data
 * @param validRanges List of valid ranges for this symbol
 */
@Serializable
data class ChartMetaDto(
    val currency: String? = null,
    val symbol: String? = null,
    val exchangeName: String? = null,
    val fullExchangeName: String? = null,
    val instrumentType: String? = null,
    @Serializable(with = NullableLongSerializer::class)
    val firstTradeDate: Long? = null,
    @Serializable(with = NullableLongSerializer::class)
    val regularMarketTime: Long? = null,
    @Serializable(with = NullableDoubleSerializer::class)
    val regularMarketPrice: Double? = null,
    @Serializable(with = NullableDoubleSerializer::class)
    val chartPreviousClose: Double? = null,
    val priceHint: Int? = null,
    val currentTradingPeriod: TradingPeriodDto? = null,
    val dataGranularity: String? = null,
    val range: String? = null,
    val validRanges: List<String>? = null,
)

/**
 * DTO for trading period information.
 *
 * @param timezone The timezone of the trading period
 * @param start The start timestamp of the trading period
 * @param end The end timestamp of the trading period
 * @param gmtoffset The GMT offset in seconds
 */
@Serializable
data class TradingPeriodDto(
    val timezone: String? = null,
    val start: Long? = null,
    val end: Long? = null,
    val gmtoffset: Int? = null,
)

/**
 * DTO for a single candle (OHLCV data point) from the v2 API.
 *
 * @param timestamp The timestamp of the candle (Unix timestamp in seconds)
 * @param open The opening price
 * @param high The highest price
 * @param low The lowest price
 * @param close The closing price
 * @param volume The volume traded
 * @param adjClose The adjusted closing price (for dividends and splits)
 */
@Serializable
data class CandleDto(
    val timestamp: Long,
    @Serializable(with = NullableDoubleSerializer::class)
    val open: Double? = null,
    @Serializable(with = NullableDoubleSerializer::class)
    val high: Double? = null,
    @Serializable(with = NullableDoubleSerializer::class)
    val low: Double? = null,
    @Serializable(with = NullableDoubleSerializer::class)
    val close: Double? = null,
    @Serializable(with = NullableLongSerializer::class)
    val volume: Long? = null,
    @Serializable(with = NullableDoubleSerializer::class)
    val adjClose: Double? = null,
)
