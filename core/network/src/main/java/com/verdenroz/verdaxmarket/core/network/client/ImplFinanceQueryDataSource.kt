package com.verdenroz.verdaxmarket.core.network.client

import com.verdenroz.verdaxmarket.core.common.enums.Interval
import com.verdenroz.verdaxmarket.core.common.enums.TimePeriod
import com.verdenroz.verdaxmarket.core.common.error.HttpException
import com.verdenroz.verdaxmarket.core.common.error.NetworkException
import com.verdenroz.verdaxmarket.core.network.BuildConfig
import com.verdenroz.verdaxmarket.core.network.FinanceQueryDataSource
import com.verdenroz.verdaxmarket.core.network.di.NetworkModule.executeAsync
import com.verdenroz.verdaxmarket.core.network.model.AnalysisResponse
import com.verdenroz.verdaxmarket.core.network.model.FullQuoteResponse
import com.verdenroz.verdaxmarket.core.network.model.HistoricalDataResponse
import com.verdenroz.verdaxmarket.core.network.model.IndexResponse
import com.verdenroz.verdaxmarket.core.network.model.MarketMoverResponse
import com.verdenroz.verdaxmarket.core.network.model.NewsResponse
import com.verdenroz.verdaxmarket.core.network.model.SectorResponse
import com.verdenroz.verdaxmarket.core.network.model.SimpleQuoteResponse
import com.verdenroz.verdaxmarket.core.network.model.TimeSeriesResponse
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.InputStream
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton


@OptIn(ExperimentalSerializationApi::class)
@Singleton
class ImplFinanceQueryDataSource @Inject constructor(
    private val parser: Json,
    private val client: OkHttpClient,
) : FinanceQueryDataSource {

    companion object {
        private val FINANCE_QUERY_API_URL =
            "https://43pk30s7aj.execute-api.us-east-2.amazonaws.com/prod/v1".toHttpUrl()
    }

    override suspend fun getByteStream(url: HttpUrl): InputStream {
        val request = Request.Builder()
            .url(url)
            .addHeader("x-api-key", BuildConfig.financeQueryAPIKey)
            .build()
        val call = client.newCall(request)
        try {
            val response = call.executeAsync()
            if (!response.isSuccessful) {
                throw HttpException(code = response.code)
            }
            return response.body!!.byteStream()
        } catch (e: Exception) {
            throw NetworkException(e)
        }
    }

    override suspend fun getQuote(symbol: String): FullQuoteResponse {
        val stream = getByteStream(
            FINANCE_QUERY_API_URL.newBuilder().apply {
                addPathSegments("quotes")
                addQueryParameter("symbols", symbol)
            }.build()
        )

        val quoteResponseList: List<FullQuoteResponse> =
            parser.decodeFromStream(ListSerializer(FullQuoteResponse.serializer()), stream)

        return quoteResponseList.first()
    }

    override suspend fun getSimpleQuote(symbol: String): SimpleQuoteResponse {
        val stream = getByteStream(
            FINANCE_QUERY_API_URL.newBuilder().apply {
                addPathSegments("simple-quotes")
                addQueryParameter("symbols", symbol)
            }.build()
        )

        val quoteResponseList: List<SimpleQuoteResponse> =
            parser.decodeFromStream(ListSerializer(SimpleQuoteResponse.serializer()), stream)

        return quoteResponseList.first()
    }

    override suspend fun getBulkQuote(symbols: List<String>): List<SimpleQuoteResponse> {
        val symbolList = symbols.joinToString(",")
        val stream = getByteStream(
            FINANCE_QUERY_API_URL.newBuilder().apply {
                addPathSegments("simple-quotes")
                addQueryParameter("symbols", symbolList)
            }.build()
        )

        val quoteResponseList: List<SimpleQuoteResponse> =
            parser.decodeFromStream(ListSerializer(SimpleQuoteResponse.serializer()), stream)

        return quoteResponseList
    }

    override suspend fun getHistoricalData(
        symbol: String,
        time: TimePeriod,
        interval: Interval
    ): Map<String, HistoricalDataResponse> {
        val stream = getByteStream(
            FINANCE_QUERY_API_URL.newBuilder().apply {
                addPathSegments("historical")
                addQueryParameter("symbol", symbol)
                addQueryParameter("time", time.value)
                addQueryParameter("interval", interval.value)
            }.build()
        )

        val timeSeriesResponse: TimeSeriesResponse =
            parser.decodeFromStream(TimeSeriesResponse.serializer(), stream)

        // Map each date and HistoricalData from TimeSeriesResponse
        return timeSeriesResponse.data.mapKeys { entry ->
            val dateTimeString = entry.key
            val formatterWithoutTime = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val formatter24Hour = DateTimeFormatter.ofPattern("yyyy-MM-dd H:mm:ss")
            val formatterWithTime = DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a")

            val dateTime = if (dateTimeString.contains(" ")) {
                LocalDateTime.parse(dateTimeString, formatter24Hour)
            } else {
                LocalDate.parse(dateTimeString, formatterWithoutTime).atStartOfDay()
            }

            dateTime.format(formatterWithTime)
        }
    }

    override suspend fun getIndexes(): List<IndexResponse> {
        val stream = getByteStream(
            FINANCE_QUERY_API_URL.newBuilder().apply {
                addPathSegments("indices")
            }.build()
        )

        val indexResponseList: List<IndexResponse> =
            parser.decodeFromStream(ListSerializer(IndexResponse.serializer()), stream)

        return indexResponseList
    }

    override suspend fun getSectors(): List<SectorResponse> {
        val stream = getByteStream(
            FINANCE_QUERY_API_URL.newBuilder().apply {
                addPathSegments("sectors")
            }.build()
        )

        val sectorResponseList: List<SectorResponse> =
            parser.decodeFromStream(ListSerializer(SectorResponse.serializer()), stream)

        return sectorResponseList
    }

    override suspend fun getSectorBySymbol(symbol: String): SectorResponse {
        val stream = getByteStream(
            FINANCE_QUERY_API_URL.newBuilder().apply {
                addPathSegments("sectors")
                addQueryParameter("symbol", symbol)
            }.build()
        )

        val sectorResponseList: List<SectorResponse> =
            parser.decodeFromStream(ListSerializer(SectorResponse.serializer()), stream)

        return sectorResponseList.first()
    }

    override suspend fun getActives(): List<MarketMoverResponse> {
        val stream = getByteStream(
            FINANCE_QUERY_API_URL.newBuilder().apply {
                addPathSegments("actives")
            }.build()
        )

        val marketMoverResponseList: List<MarketMoverResponse> =
            parser.decodeFromStream(ListSerializer(MarketMoverResponse.serializer()), stream)

        return marketMoverResponseList
    }

    override suspend fun getGainers(): List<MarketMoverResponse> {
        val stream = getByteStream(
            FINANCE_QUERY_API_URL.newBuilder().apply {
                addPathSegments("gainers")
            }.build()
        )

        val marketMoverResponseList: List<MarketMoverResponse> =
            parser.decodeFromStream(ListSerializer(MarketMoverResponse.serializer()), stream)

        return marketMoverResponseList
    }

    override suspend fun getLosers(): List<MarketMoverResponse> {
        val stream = getByteStream(
            FINANCE_QUERY_API_URL.newBuilder().apply {
                addPathSegments("losers")
            }.build()
        )

        val marketMoverResponseList: List<MarketMoverResponse> =
            parser.decodeFromStream(ListSerializer(MarketMoverResponse.serializer()), stream)

        return marketMoverResponseList
    }

    override suspend fun getNews(): List<NewsResponse> {
        val stream = getByteStream(
            FINANCE_QUERY_API_URL.newBuilder().apply {
                addPathSegments("news")
            }.build()
        )

        val newsResponseList: List<NewsResponse> =
            parser.decodeFromStream(ListSerializer(NewsResponse.serializer()), stream)

        return newsResponseList.shuffled()
    }

    override suspend fun getNewsForSymbol(symbol: String): List<NewsResponse> {
        val stream = getByteStream(
            FINANCE_QUERY_API_URL.newBuilder().apply {
                addPathSegments("news")
                addQueryParameter("symbol", symbol)
            }.build()
        )

        val newsResponseList: List<NewsResponse> =
            parser.decodeFromStream(ListSerializer(NewsResponse.serializer()), stream)

        return newsResponseList
    }

    override suspend fun getSimilarSymbols(symbol: String): List<SimpleQuoteResponse> {
        val stream = getByteStream(
            FINANCE_QUERY_API_URL.newBuilder().apply {
                addPathSegments("similar-stocks")
                addQueryParameter("symbol", symbol)
            }.build()
        )

        val quoteResponseList: List<SimpleQuoteResponse> =
            parser.decodeFromStream(ListSerializer(SimpleQuoteResponse.serializer()), stream)

        return quoteResponseList
    }

    override suspend fun getSummaryAnalysis(symbol: String, interval: Interval): AnalysisResponse {
        val stream = getByteStream(
            FINANCE_QUERY_API_URL.newBuilder().apply {
                addPathSegments("analysis")
                addQueryParameter("symbol", symbol)
                addQueryParameter("interval", interval.value)
            }.build()
        )

        val analysisResponse: AnalysisResponse =
            parser.decodeFromStream(AnalysisResponse.serializer(), stream)

        return analysisResponse
    }
}