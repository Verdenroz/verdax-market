package com.verdenroz.verdaxmarket.core.data.repository

import com.verdenroz.verdaxmarket.core.common.dispatchers.Dispatcher
import com.verdenroz.verdaxmarket.core.common.dispatchers.FinanceQueryDispatchers
import com.verdenroz.verdaxmarket.core.common.error.DataError
import com.verdenroz.verdaxmarket.core.common.result.Result
import com.verdenroz.verdaxmarket.core.data.model.asEntity
import com.verdenroz.verdaxmarket.core.data.model.toExternal
import com.verdenroz.verdaxmarket.core.data.repository.WatchlistRepository.Companion.WATCHLIST_REFRESH_CLOSED
import com.verdenroz.verdaxmarket.core.data.repository.WatchlistRepository.Companion.WATCHLIST_REFRESH_OPEN
import com.verdenroz.verdaxmarket.core.data.utils.MarketStatusMonitor
import com.verdenroz.verdaxmarket.core.data.utils.handleLocalException
import com.verdenroz.verdaxmarket.core.database.dao.QuoteDao
import com.verdenroz.verdaxmarket.core.model.SimpleQuoteData
import com.verdenroz.verdaxmarket.core.network.FinanceQueryDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImplWatchlistRepository @Inject constructor(
    private val quotesDao: QuoteDao,
    private val api: FinanceQueryDataSource,
    marketStatusMonitor: MarketStatusMonitor,
    @Dispatcher(FinanceQueryDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) : WatchlistRepository {

    override val isOpen: Flow<Boolean> = marketStatusMonitor.isMarketOpen.stateIn(
        CoroutineScope(ioDispatcher),
        SharingStarted.WhileSubscribed(5000L),
        marketStatusMonitor.isMarketOpen()
    )

    override val watchlist: Flow<Result<List<SimpleQuoteData>, DataError.Local>> =
        isOpen.flatMapLatest { isOpen ->
            flow {
                while (true) {
                    try {
                        val quotes = quotesDao.getAllQuoteData().toExternal()
                        emit(Result.Success(quotes))
                    } catch (e: Exception) {
                        emit(handleLocalException(e))
                    }
                    val refreshInterval =
                        if (isOpen) WATCHLIST_REFRESH_OPEN else WATCHLIST_REFRESH_CLOSED // 30 seconds or 10 minutes
                    delay(refreshInterval)
                }
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

    override fun isSymbolInWatchlist(symbol: String): Flow<Boolean> =
        quotesDao.getAllQuoteDataFlow().map { quotes ->
            quotes.any { it.symbol == symbol }
        }.flowOn(ioDispatcher)

}