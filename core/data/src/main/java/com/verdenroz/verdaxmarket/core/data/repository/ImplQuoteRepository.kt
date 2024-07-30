package com.verdenroz.verdaxmarket.core.data.repository

import com.verdenroz.verdaxmarket.core.common.dispatchers.Dispatcher
import com.verdenroz.verdaxmarket.core.common.dispatchers.FinanceQueryDispatchers
import com.verdenroz.verdaxmarket.core.common.enums.Interval
import com.verdenroz.verdaxmarket.core.common.enums.TimePeriod
import com.verdenroz.verdaxmarket.core.common.error.DataError
import com.verdenroz.verdaxmarket.core.common.result.Result
import com.verdenroz.verdaxmarket.core.data.model.asExternalModel
import com.verdenroz.verdaxmarket.core.data.utils.handleNetworkException
import com.verdenroz.verdaxmarket.core.model.FullQuoteData
import com.verdenroz.verdaxmarket.core.model.HistoricalData
import com.verdenroz.verdaxmarket.core.model.MarketSector
import com.verdenroz.verdaxmarket.core.model.News
import com.verdenroz.verdaxmarket.core.model.QuoteAnalysis
import com.verdenroz.verdaxmarket.core.model.SimpleQuoteData
import com.verdenroz.verdaxmarket.core.network.FinanceQueryDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ImplQuoteRepository @Inject constructor(
    private val api: FinanceQueryDataSource,
    @Dispatcher(FinanceQueryDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) : QuoteRepository {

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