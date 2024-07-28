package com.verdenroz.verdaxmarket.data.repository

import com.verdenroz.verdaxmarket.common.dispatchers.Dispatcher
import com.verdenroz.verdaxmarket.common.dispatchers.FinanceQueryDispatchers
import com.verdenroz.verdaxmarket.common.error.DataError
import com.verdenroz.verdaxmarket.common.result.Result
import com.verdenroz.verdaxmarket.data.model.asEntity
import com.verdenroz.verdaxmarket.data.model.asExternalModel
import com.verdenroz.verdaxmarket.data.utils.MarketStatusMonitor
import com.verdenroz.verdaxmarket.data.utils.handleLocalException
import com.verdenroz.verdaxmarket.database.dao.QuoteDao
import com.verdenroz.verdaxmarket.model.SimpleQuoteData
import com.verdenroz.verdaxmarket.network.FinanceQueryDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class ImplWatchlistRepository @Inject constructor(
    private val quotesDao: QuoteDao,
    private val api: FinanceQueryDataSource,
    marketStatusMonitor: MarketStatusMonitor,
    @Dispatcher(FinanceQueryDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) : WatchlistRepository {

    override val marketStatus: Flow<Boolean> = marketStatusMonitor.isMarketOpen.stateIn(
        CoroutineScope(ioDispatcher),
        SharingStarted.WhileSubscribed(5000L),
        true
    )

    override val watchlist: Flow<Result<List<SimpleQuoteData>, DataError.Local>> = combine(
        marketStatus,
        flow {
            while (true) {
                try {
                    val quotes = quotesDao.getAllQuoteData().asExternalModel()
                    emit(Result.Success(quotes))
                } catch (e: Exception) {
                    emit(handleLocalException(e))
                }
            }
        }
    ) { isOpen, quotesResult ->
        Pair(isOpen, quotesResult)
    }.flatMapLatest { (isOpen, quotesResult) ->
        flow {
            emit(quotesResult)
            val refreshInterval: Long = if (isOpen) 30000L else 600000L // 30 seconds or 10 minute
            delay(refreshInterval)
        }
    }.flowOn(ioDispatcher)

    override suspend fun refreshWatchList(): Result<Unit, DataError.Local> {
        return withContext(ioDispatcher) {
            try {
                val symbols = quotesDao.getAllQuoteData().map { it.symbol }
                if (symbols.isNotEmpty()) {
                    val updatedQuotes = api.getBulkQuote(symbols).asEntity()
                    quotesDao.updateAll(updatedQuotes)
                }
                Result.Success(Unit)
            } catch (e: Exception) {
                handleLocalException(e)
            }
        }
    }

    override suspend fun addToWatchList(symbol: String): Result<Unit, DataError.Local> {
        return withContext(ioDispatcher) {
            try {
                val quote = api.getSimpleQuote(symbol)
                quotesDao.insert(quote.asEntity())
                Result.Success(Unit)
            } catch (e: Exception) {
                handleLocalException(e)
            }
        }
    }

    override suspend fun deleteFromWatchList(symbol: String): Result<Unit, DataError.Local> {
        return withContext(ioDispatcher) {
            try {
                quotesDao.delete(symbol)
                Result.Success(Unit)
            } catch (e: Exception) {
                handleLocalException(e)
            }
        }
    }

    override suspend fun clearWatchList(): Result<Unit, DataError.Local> {
        return withContext(ioDispatcher) {
            try {
                quotesDao.deleteAll()
                Result.Success(Unit)
            } catch (e: Exception) {
                handleLocalException(e)
            }
        }
    }

}