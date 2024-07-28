package com.verdenroz.verdaxmarket.data.repository

import com.verdenroz.verdaxmarket.common.dispatchers.Dispatcher
import com.verdenroz.verdaxmarket.common.dispatchers.FinanceQueryDispatchers
import com.verdenroz.verdaxmarket.common.enums.Interval
import com.verdenroz.verdaxmarket.common.enums.TimePeriod
import com.verdenroz.verdaxmarket.common.error.DataError
import com.verdenroz.verdaxmarket.common.result.Result
import com.verdenroz.verdaxmarket.data.model.asExternalModel
import com.verdenroz.verdaxmarket.data.repository.FinanceQueryRepository.Companion.NEWS_SECTORS_REFRESH_INTERVAL
import com.verdenroz.verdaxmarket.data.utils.handleNetworkException
import com.verdenroz.verdaxmarket.model.FullQuoteData
import com.verdenroz.verdaxmarket.model.HistoricalData
import com.verdenroz.verdaxmarket.model.MarketIndex
import com.verdenroz.verdaxmarket.model.MarketMover
import com.verdenroz.verdaxmarket.model.MarketSector
import com.verdenroz.verdaxmarket.model.News
import com.verdenroz.verdaxmarket.model.QuoteAnalysis
import com.verdenroz.verdaxmarket.model.SimpleQuoteData
import com.verdenroz.verdaxmarket.network.FinanceQueryDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

internal class ImplFinanceQueryRepository @Inject constructor(
    private val api: FinanceQueryDataSource,
    @Dispatcher(FinanceQueryDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) : FinanceQueryRepository {

    override val indices: Flow<Result<List<MarketIndex>, DataError.Network>> = flow {
        while (true) {
            try {
                val indexes = api.getIndexes().asExternalModel()
                emit(Result.Success(indexes))
                delay(NEWS_SECTORS_REFRESH_INTERVAL)
            } catch (e: Exception) {
                emit(handleNetworkException(e))
            }
        }
    }.flowOn(ioDispatcher)

    override val actives: Flow<Result<List<MarketMover>, DataError.Network>> = flow {
        while (true) {
            try {
                val actives = api.getActives().asExternalModel()
                emit(Result.Success(actives))
                delay(NEWS_SECTORS_REFRESH_INTERVAL)
            } catch (e: Exception) {
                emit(handleNetworkException(e))
            }
        }
    }.flowOn(ioDispatcher)

    override val losers: Flow<Result<List<MarketMover>, DataError.Network>> = flow {
        while (true) {
            try {
                val losers = api.getLosers().asExternalModel()
                emit(Result.Success(losers))
                delay(NEWS_SECTORS_REFRESH_INTERVAL)
            } catch (e: Exception) {
                emit(handleNetworkException(e))
            }
        }
    }.flowOn(ioDispatcher)

    override val gainers: Flow<Result<List<MarketMover>, DataError.Network>> = flow {
        while (true) {
            try {
                val gainers = api.getGainers().asExternalModel()
                emit(Result.Success(gainers))
                delay(NEWS_SECTORS_REFRESH_INTERVAL)
            } catch (e: Exception) {
                emit(handleNetworkException(e))
            }
        }
    }.flowOn(ioDispatcher)

    override val headlines: Flow<Result<List<News>, DataError.Network>> = flow {
        while (true) {
            try {
                val news = api.getNews().asExternalModel()
                emit(Result.Success(news))
                delay(NEWS_SECTORS_REFRESH_INTERVAL)
            } catch (e: Exception) {
                emit(handleNetworkException(e))
            }
        }
    }.flowOn(ioDispatcher)

    override val sectors: Flow<Result<List<MarketSector>, DataError.Network>> = flow {
        while (true) {
            try {
                val sectors = api.getSectors().asExternalModel()
                emit(Result.Success(sectors))
                delay(NEWS_SECTORS_REFRESH_INTERVAL)
            } catch (e: Exception) {
                emit(handleNetworkException(e))
            }
        }
    }.flowOn(ioDispatcher)


    override fun getFullQuote(symbol: String): Flow<Result<FullQuoteData, DataError.Network>> =
        flow {
            try {
                val quote = api.getQuote(symbol).asExternalModel()
                emit(Result.Success(quote))
            } catch (e: Exception) {
                emit(handleNetworkException(e))
            }
        }.flowOn(ioDispatcher)

    override suspend fun getSimpleQuote(symbol: String): Flow<Result<SimpleQuoteData, DataError.Network>> =
        flow {
            try {
                val quote = api.getSimpleQuote(symbol).asExternalModel()
                emit(Result.Success(quote))
            } catch (e: Exception) {
                emit(handleNetworkException(e))
            }
        }.flowOn(ioDispatcher)

    override suspend fun getBulkQuote(symbols: List<String>): Flow<Result<List<SimpleQuoteData>, DataError.Network>> =
        flow {
            try {
                val quote = api.getBulkQuote(symbols).asExternalModel()
                emit(Result.Success(quote))
            } catch (e: Exception) {
                emit(handleNetworkException(e))
            }
        }.flowOn(ioDispatcher)

    override fun getNewsForSymbol(symbol: String): Flow<Result<List<News>, DataError.Network>> =
        flow {
            try {
                val quote = api.getNewsForSymbol(symbol).asExternalModel()
                emit(Result.Success(quote))
            } catch (e: Exception) {
                emit(handleNetworkException(e))
            }
        }.flowOn(ioDispatcher)

    override fun getSimilarStocks(symbol: String): Flow<Result<List<SimpleQuoteData>, DataError.Network>> =
        flow {
            try {
                val quote = api.getSimilarSymbols(symbol).asExternalModel()
                emit(Result.Success(quote))
            } catch (e: Exception) {
                emit(handleNetworkException(e))
            }
        }.flowOn(ioDispatcher)

    override fun getSectorBySymbol(symbol: String): Flow<Result<MarketSector?, DataError.Network>> =
        flow {
            try {
                val quote = api.getSectorBySymbol(symbol).asExternalModel()
                emit(Result.Success(quote))
            } catch (e: Exception) {
                emit(handleNetworkException(e))
            }
        }.flowOn(ioDispatcher)

    override suspend fun getTimeSeries(
        symbol: String,
        timePeriod: TimePeriod,
        interval: Interval,
    ): Flow<Result<Map<String, HistoricalData>, DataError.Network>> = flow {
        try {
            val quote = api.getHistoricalData(symbol, timePeriod, interval).asExternalModel()
            emit(Result.Success(quote))
        } catch (e: Exception) {
            emit(handleNetworkException(e))
        }
    }.flowOn(ioDispatcher)

    override fun getAnalysis(
        symbol: String,
        interval: Interval
    ): Flow<Result<QuoteAnalysis?, DataError.Network>> = flow {
        try {
            val analysis = api.getSummaryAnalysis(symbol, interval).asExternalModel()
            emit(Result.Success(analysis))
        } catch (e: Exception) {
            emit(handleNetworkException(e))
        }
    }.flowOn(ioDispatcher)

}