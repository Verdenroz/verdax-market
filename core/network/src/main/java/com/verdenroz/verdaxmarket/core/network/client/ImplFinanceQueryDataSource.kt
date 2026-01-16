package com.verdenroz.verdaxmarket.core.network.client

import com.verdenroz.verdaxmarket.core.common.enums.Interval
import com.verdenroz.verdaxmarket.core.common.enums.SectorType
import com.verdenroz.verdaxmarket.core.common.enums.TimePeriod
import com.verdenroz.verdaxmarket.core.common.enums.toDisplayName
import com.verdenroz.verdaxmarket.core.common.error.HttpException
import com.verdenroz.verdaxmarket.core.common.error.NetworkException
import com.verdenroz.verdaxmarket.core.network.FinanceQueryDataSource
import com.verdenroz.verdaxmarket.core.network.FinanceQueryDataSource.Companion.FINANCE_QUERY_API_URL
import com.verdenroz.verdaxmarket.core.network.di.NetworkModule.executeAsync
import com.verdenroz.verdaxmarket.core.network.model.AnalysisDto
import com.verdenroz.verdaxmarket.core.network.model.IndexDto
import com.verdenroz.verdaxmarket.core.network.model.QuoteDto
import com.verdenroz.verdaxmarket.core.network.model.BatchQuotesDto
import com.verdenroz.verdaxmarket.core.network.model.ChartDto
import com.verdenroz.verdaxmarket.core.network.model.NewsWrapperDto
import com.verdenroz.verdaxmarket.core.network.model.NewsArticleDto
import com.verdenroz.verdaxmarket.core.network.model.ScreenersWrapperDto
import com.verdenroz.verdaxmarket.core.network.model.SectorDetailDto
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.InputStream
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton


@OptIn(ExperimentalSerializationApi::class)
@Singleton
class ImplFinanceQueryDataSource @Inject constructor(
    private val parser: Json,
    private val client: OkHttpClient,
) : FinanceQueryDataSource {

    override suspend fun getByteStream(url: HttpUrl): InputStream {
        val request = Request.Builder()
            .url(url)
            .build()
        val call = client.newCall(request)
        try {
            val response = call.executeAsync()
            if (!response.isSuccessful) {
                throw HttpException(code = response.code)
            }
            return response.body!!.byteStream()
        } catch (e: UnknownHostException) {
            throw NetworkException(e)
        } catch (e: SocketTimeoutException) {
            throw NetworkException(e)
        }
    }

    override suspend fun getQuote(symbol: String): QuoteDto {
        val stream = getByteStream(
            FINANCE_QUERY_API_URL.newBuilder().apply {
                addPathSegments("v2/quote/$symbol")
                addQueryParameter("format", "raw")
                addQueryParameter("logo", "true")
            }.build()
        )

        return parser.decodeFromStream(QuoteDto.serializer(), stream)
    }

    override suspend fun getSimpleQuote(symbol: String): QuoteDto {
        val stream = getByteStream(
            FINANCE_QUERY_API_URL.newBuilder().apply {
                addPathSegments("v2/quote/$symbol")
                addQueryParameter("format", "raw")
            }.build()
        )

        return parser.decodeFromStream(QuoteDto.serializer(), stream)
    }

    override suspend fun getBulkQuote(symbols: List<String>): BatchQuotesDto {
        val symbolList = symbols.joinToString(",")
        val stream = getByteStream(
            FINANCE_QUERY_API_URL.newBuilder().apply {
                addPathSegments("v2/quotes")
                addQueryParameter("symbols", symbolList)
                addQueryParameter("format", "raw")
                addQueryParameter("logo", "true")
            }.build()
        )

        return parser.decodeFromStream(BatchQuotesDto.serializer(), stream)
    }

    override suspend fun getHistoricalData(
        symbol: String,
        time: TimePeriod,
        interval: Interval
    ): ChartDto {
        val stream = getByteStream(
            FINANCE_QUERY_API_URL.newBuilder().apply {
                addPathSegments("v2/chart/$symbol")
                addQueryParameter("range", time.value)
                addQueryParameter("interval", interval.value)
                addQueryParameter("format", "raw")
            }.build()
        )

        return parser.decodeFromStream(ChartDto.serializer(), stream)
    }

    override suspend fun getIndexes(): List<IndexDto> {
        val stream = getByteStream(
            FINANCE_QUERY_API_URL.newBuilder().apply {
                addPathSegments("v2/indices")
                addQueryParameter("format", "raw")
            }.build()
        )

        // v2 API returns BatchQuotesDto format for indices
        val batchQuotes = parser.decodeFromStream(BatchQuotesDto.serializer(), stream)

        // Convert QuoteDto to IndexDto
        return batchQuotes.quotes.values.map { quote ->
            IndexDto(
                name = quote.name ?: quote.symbol,
                value = quote.regularMarketPrice ?: 0.0,
                change = quote.regularMarketChange?.toString() ?: "0.0",
                percentChange = quote.regularMarketChangePercent?.toString() ?: "0.0",
                fiveDaysReturn = quote.fiveDayReturn?.toString(),
                oneMonthReturn = quote.oneMonthReturn?.toString(),
                sixMonthReturn = quote.sixMonthReturn?.toString(),
                ytdReturn = quote.ytdReturn?.toString(),
                yearReturn = quote.oneYearReturn?.toString(),
                fiveYearReturn = quote.fiveYearReturn?.toString(),
            )
        }
    }

    override suspend fun getSectors(): List<SectorDetailDto> = coroutineScope {
        // Fetch all 11 sectors in parallel
        SectorType.entries.map { sectorType ->
            async { getSector(sectorType) }
        }.awaitAll()
    }

    override suspend fun getSector(sectorType: SectorType): SectorDetailDto {
        val stream = getByteStream(
            FINANCE_QUERY_API_URL.newBuilder().apply {
                addPathSegments("v2/sectors/${sectorType.value}")
            }.build()
        )

        val dto = parser.decodeFromStream(SectorDetailDto.serializer(), stream)

        // If API doesn't return the name, populate it from the SectorType
        return if (dto.name == null) {
            dto.copy(name = sectorType.toDisplayName())
        } else {
            dto
        }
    }

    override suspend fun getActives(): ScreenersWrapperDto {
        val stream = getByteStream(
            FINANCE_QUERY_API_URL.newBuilder().apply {
                addPathSegments("v2/screeners/most_actives")
                addQueryParameter("format", "raw")
            }.build()
        )

        return parser.decodeFromStream(ScreenersWrapperDto.serializer(), stream)
    }

    override suspend fun getGainers(): ScreenersWrapperDto {
        val stream = getByteStream(
            FINANCE_QUERY_API_URL.newBuilder().apply {
                addPathSegments("v2/screeners/day_gainers")
                addQueryParameter("format", "raw")
            }.build()
        )

        return parser.decodeFromStream(ScreenersWrapperDto.serializer(), stream)
    }

    override suspend fun getLosers(): ScreenersWrapperDto {
        val stream = getByteStream(
            FINANCE_QUERY_API_URL.newBuilder().apply {
                addPathSegments("v2/screeners/day_losers")
                addQueryParameter("format", "raw")
            }.build()
        )

        return parser.decodeFromStream(ScreenersWrapperDto.serializer(), stream)
    }

    override suspend fun getNews(): NewsWrapperDto {
        val stream = getByteStream(
            FINANCE_QUERY_API_URL.newBuilder().apply {
                addPathSegments("v2/news")
            }.build()
        )

        // v2 API returns plain array instead of wrapper object
        val newsArticles = parser.decodeFromStream(ListSerializer(NewsArticleDto.serializer()), stream)
        return NewsWrapperDto(count = newsArticles.size, news = newsArticles)
    }

    override suspend fun getNewsForSymbol(symbol: String): NewsWrapperDto {
        val stream = getByteStream(
            FINANCE_QUERY_API_URL.newBuilder().apply {
                addPathSegments("v2/news/$symbol")
            }.build()
        )

        // v2 API returns plain array instead of wrapper object
        val newsArticles = parser.decodeFromStream(ListSerializer(NewsArticleDto.serializer()), stream)
        return NewsWrapperDto(count = newsArticles.size, news = newsArticles)
    }

    override suspend fun getSimilarSymbols(symbol: String): BatchQuotesDto {
        val stream = getByteStream(
            FINANCE_QUERY_API_URL.newBuilder().apply {
                addPathSegments("v2/recommendations/$symbol")
                addQueryParameter("format", "raw")
            }.build()
        )

        return parser.decodeFromStream(BatchQuotesDto.serializer(), stream)
    }

    override suspend fun getSummaryAnalysis(symbol: String, interval: Interval): AnalysisDto {
        val stream = getByteStream(
            FINANCE_QUERY_API_URL.newBuilder().apply {
                addPathSegments("v2/indicators/$symbol")
                addQueryParameter("interval", interval.value)
            }.build()
        )

        return parser.decodeFromStream(AnalysisDto.serializer(), stream)
    }
}