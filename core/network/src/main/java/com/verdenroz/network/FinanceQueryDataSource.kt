package com.verdenroz.network

import com.verdenroz.common.enums.Interval
import com.verdenroz.common.enums.TimePeriod
import com.verdenroz.network.model.AnalysisResponse
import com.verdenroz.network.model.FullQuoteResponse
import com.verdenroz.network.model.HistoricalDataResponse
import com.verdenroz.network.model.IndexResponse
import com.verdenroz.network.model.MarketMoverResponse
import com.verdenroz.network.model.NewsResponse
import com.verdenroz.network.model.SectorResponse
import com.verdenroz.network.model.SimpleQuoteResponse
import kotlinx.serialization.SerializationException
import com.verdenroz.common.error.HttpException
import com.verdenroz.common.error.NetworkException
import okhttp3.HttpUrl
import java.io.InputStream

interface FinanceQueryDataSource {

    /**
     * Get a byte stream from a given URL
     * @throws [HttpException] if the request fails
     * @throws [NetworkException] if the network fails
     */
    suspend fun getByteStream(url: HttpUrl): InputStream

    /**
     * Get full quote data for a stock with all available information
     * @param symbol identifies the quote is for
     * @return [FullQuoteResponse] containing all available information such as price, volume, market cap, etc.
     * @throws [SerializationException] if the response cannot be parsed
     */
    suspend fun getQuote(symbol: String): FullQuoteResponse

    /**
     * Get simple quote data for a stock with basic information
     * @param symbol identifies the quote is for
     * @return [SimpleQuoteResponse] containing basic information such as price, change, percent change, etc.
     * @throws [SerializationException] if the response cannot be parsed
     */
    suspend fun getSimpleQuote(symbol: String): SimpleQuoteResponse

    /**
     * Get simple quote data for a list of stocks
     * @param symbols a variable number of strings to identify the requested quote
     * @return a list of [SimpleQuoteResponse]
     * @throws [SerializationException] if the response cannot be parsed
     */
    suspend fun getBulkQuote(symbols: List<String>): List<SimpleQuoteResponse>

    /**
     * Get historical prices (OHLCV) for a stock
     * @param symbol symbol of the stock
     * @param time the time period to get data for (1d, 5d, 1m, 3m, 6m, 1y, 2y, 5y, 10y, ytd, max)
     * @param interval the interval between data points (15m, 30m, 1h, 1d, 1wk, 1mo, 3mo)
     * @return a map of dates to [HistoricalDataResponse]
     * @see Interval
     * @see TimePeriod
     * @throws [SerializationException] if the response cannot be parsed
     */
    suspend fun getHistoricalData(symbol: String, time: TimePeriod, interval: Interval): Map<String, HistoricalDataResponse>

    /**
     * Get current market indices in the US
     * @return a list of [IndexResponse]
     * @throws [SerializationException] if the response cannot be parsed
     */
    suspend fun getIndices(): List<IndexResponse>


    /**
     * Get market sectors in the US
     * @return a list of [SectorResponse]
     * @throws [SerializationException] if the response cannot be parsed
     */
    suspend fun getSectors(): List<SectorResponse>

    /**
     * Get a specific market sector by symbol
     * @return [SectorResponse]
     * @throws [SerializationException] if the response cannot be parsed
     */
    suspend fun getSectorBySymbol(symbol: String): SectorResponse

    /**
     * Get active stocks in the US
     * @return a list of [MarketMoverResponse]
     * @throws [SerializationException] if the response cannot be parsed
     */
    suspend fun getActives(): List<MarketMoverResponse>

    /**
     * Get stocks with the highest percentage gain in the US
     * @return a list of [MarketMoverResponse]
     * @throws [SerializationException] if the response cannot be parsed
     */
    suspend fun getGainers(): List<MarketMoverResponse>

    /**
     * Get stocks with the highest percentage loss in the US
     * @return a list of [MarketMoverResponse]
     * @throws [SerializationException] if the response cannot be parsed
     */
    suspend fun getLosers(): List<MarketMoverResponse>

    /**
     * Get the latest general financial news
     * @return a list of [NewsResponse]
     * @throws [SerializationException] if the response cannot be parsed
     */
    suspend fun getNews(): List<NewsResponse>

    /**
     * Get the latest financial news for a stock
     * @param symbol the stock to get news for
     * @return a list of [NewsResponse]
     * @throws [SerializationException] if the response cannot be parsed
     */
    suspend fun getNewsForSymbol(symbol: String): List<NewsResponse>

    /**
     * Find similar stocks to a given symbol
     * @param symbol the stock to find similar stocks for
     * @return a list of [SimpleQuoteResponse]
     * @throws [SerializationException] if the response cannot be parsed
     */
    suspend fun getSimilarSymbols(symbol: String): List<SimpleQuoteResponse>

    /**
     * Get a summary analysis with multiple technical indicators (sma, ema, rsi, etc) of a stock
     * @param interval optional [Interval] to get data for (15m, 30m, 1h, 1d, 1wk, 1mo, 3mo)
     * @return [AnalysisResponse]
     * @throws [SerializationException] if the response cannot be parsed
     */
    suspend fun getSummaryAnalysis(symbol: String, interval: Interval = Interval.DAILY): AnalysisResponse
}