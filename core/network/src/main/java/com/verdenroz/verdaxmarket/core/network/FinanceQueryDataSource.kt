package com.verdenroz.verdaxmarket.core.network

import com.verdenroz.verdaxmarket.core.common.enums.Interval
import com.verdenroz.verdaxmarket.core.common.enums.SectorType
import com.verdenroz.verdaxmarket.core.common.enums.TimePeriod
import com.verdenroz.verdaxmarket.core.network.model.AnalysisDto
import com.verdenroz.verdaxmarket.core.network.model.IndexDto
import com.verdenroz.verdaxmarket.core.network.model.QuoteDto
import com.verdenroz.verdaxmarket.core.network.model.BatchQuotesDto
import com.verdenroz.verdaxmarket.core.network.model.ChartDto
import com.verdenroz.verdaxmarket.core.network.model.NewsWrapperDto
import com.verdenroz.verdaxmarket.core.network.model.ScreenersWrapperDto
import com.verdenroz.verdaxmarket.core.network.model.SectorDetailDto
import kotlinx.serialization.SerializationException
import com.verdenroz.verdaxmarket.core.common.error.HttpException
import com.verdenroz.verdaxmarket.core.common.error.NetworkException
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import java.io.InputStream

interface FinanceQueryDataSource {
    companion object {
        internal val FINANCE_QUERY_API_URL =
            "https://finance-query.com".toHttpUrl()
    }

    /**
     * Get a byte stream from a given URL
     * @throws [HttpException] if the request fails
     * @throws [NetworkException] if the network fails
     */
    suspend fun getByteStream(url: HttpUrl): InputStream

    /**
     * Get full quote data for a stock with all available information
     * @param symbol identifies the quote is for
     * @return [QuoteDto] containing all available information such as price, volume, market cap, etc.
     * @throws [SerializationException] if the response cannot be parsed
     */
    suspend fun getQuote(symbol: String): QuoteDto

    /**
     * Get simple quote data for a stock with basic information
     * @param symbol identifies the quote is for
     * @return [QuoteDto] containing basic information such as price, change, percent change, etc.
     * @throws [SerializationException] if the response cannot be parsed
     */
    suspend fun getSimpleQuote(symbol: String): QuoteDto

    /**
     * Get simple quote data for a list of stocks
     * @param symbols a variable number of strings to identify the requested quote
     * @return [BatchQuotesDto] with quotes map and errors map
     * @throws [SerializationException] if the response cannot be parsed
     */
    suspend fun getBulkQuote(symbols: List<String>): BatchQuotesDto

    /**
     * Get historical prices (OHLCV) for a stock
     * @param symbol symbol of the stock
     * @param time the time period to get data for (1d, 5d, 1m, 3m, 6m, 1y, 2y, 5y, 10y, ytd, max)
     * @param interval the interval between data points (15m, 30m, 1h, 1d, 1wk, 1mo, 3mo)
     * @return [ChartDto] containing candles array and metadata
     * @see Interval
     * @see TimePeriod
     * @throws [SerializationException] if the response cannot be parsed
     */
    suspend fun getHistoricalData(symbol: String, time: TimePeriod, interval: Interval): ChartDto

    /**
     * Get current market indices in the US
     * @return a list of [IndexDto]
     * @throws [SerializationException] if the response cannot be parsed
     */
    suspend fun getIndexes(): List<IndexDto>


    /**
     * Get market sectors in the US
     * @return a list of [SectorDetailDto]
     * @throws [SerializationException] if the response cannot be parsed
     */
    suspend fun getSectors(): List<SectorDetailDto>

    /**
     * Get a specific market sector by type
     * @param sectorType the type of sector to retrieve
     * @return [SectorDetailDto]
     * @throws [SerializationException] if the response cannot be parsed
     */
    suspend fun getSector(sectorType: SectorType): SectorDetailDto

    /**
     * Get active stocks in the US
     * @return [ScreenersWrapperDto] containing most active stocks
     * @throws [SerializationException] if the response cannot be parsed
     */
    suspend fun getActives(): ScreenersWrapperDto

    /**
     * Get stocks with the highest percentage gain in the US
     * @return [ScreenersWrapperDto] containing top gainers
     * @throws [SerializationException] if the response cannot be parsed
     */
    suspend fun getGainers(): ScreenersWrapperDto

    /**
     * Get stocks with the highest percentage loss in the US
     * @return [ScreenersWrapperDto] containing top losers
     * @throws [SerializationException] if the response cannot be parsed
     */
    suspend fun getLosers(): ScreenersWrapperDto

    /**
     * Get the latest general financial news
     * @return [NewsWrapperDto] containing news articles
     * @throws [SerializationException] if the response cannot be parsed
     */
    suspend fun getNews(): NewsWrapperDto

    /**
     * Get the latest financial news for a stock
     * @param symbol the stock to get news for
     * @return [NewsWrapperDto] containing news articles
     * @throws [SerializationException] if the response cannot be parsed
     */
    suspend fun getNewsForSymbol(symbol: String): NewsWrapperDto

    /**
     * Find similar stocks to a given symbol
     * @param symbol the stock to find similar stocks for
     * @return [BatchQuotesDto] containing similar stocks
     * @throws [SerializationException] if the response cannot be parsed
     */
    suspend fun getSimilarSymbols(symbol: String): BatchQuotesDto

    /**
     * Get a summary analysis with multiple technical indicators (sma, ema, rsi, etc) of a stock
     * @param interval optional [Interval] to get data for (15m, 30m, 1h, 1d, 1wk, 1mo, 3mo)
     * @return [AnalysisDto]
     * @throws [SerializationException] if the response cannot be parsed
     */
    suspend fun getSummaryAnalysis(symbol: String, interval: Interval = Interval.DAILY): AnalysisDto
}