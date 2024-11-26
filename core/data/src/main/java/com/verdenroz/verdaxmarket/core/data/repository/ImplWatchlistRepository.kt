package com.verdenroz.verdaxmarket.core.data.repository

import com.verdenroz.verdaxmarket.core.common.dispatchers.Dispatcher
import com.verdenroz.verdaxmarket.core.common.dispatchers.FinanceQueryDispatchers
import com.verdenroz.verdaxmarket.core.data.model.asEntity
import com.verdenroz.verdaxmarket.core.database.dao.QuoteDao
import com.verdenroz.verdaxmarket.core.database.model.asExternalModel
import com.verdenroz.verdaxmarket.core.model.SimpleQuoteData
import com.verdenroz.verdaxmarket.core.network.FinanceQueryDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImplWatchlistRepository @Inject constructor(
    private val quotesDao: QuoteDao,
    private val api: FinanceQueryDataSource,
    @Dispatcher(FinanceQueryDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) : WatchlistRepository {

    override val watchlist: Flow<List<SimpleQuoteData>> =
        quotesDao.getAllQuoteDataFlow().map { it.asExternalModel() }

    override suspend fun getWatchlist(): Flow<List<SimpleQuoteData>> =
        quotesDao.getAllQuoteDataFlow().map { it.asExternalModel() }

    override suspend fun updateWatchList(quotes: List<SimpleQuoteData>) {
        withContext(ioDispatcher) {
            val updatedQuotes = quotes.map { it.asEntity() }
            quotesDao.updateAll(updatedQuotes)
        }
    }

    override suspend fun updateWatchlist(symbols: List<String>) {
        withContext(ioDispatcher) {
            val quotes = api.getBulkQuote(symbols).asEntity()
            quotesDao.updateAll(quotes)
        }
    }

    override suspend fun addToWatchList(quote: SimpleQuoteData) {
        withContext(ioDispatcher) {
            quotesDao.insert(quote.asEntity())
        }
    }

    override suspend fun addToWatchList(symbol: String) {
        withContext(ioDispatcher) {
            val quote = api.getSimpleQuote(symbol).asEntity()
            quotesDao.insert(quote)
        }
    }

    override suspend fun addPlaceholderToWatchList(symbol: String) {
        withContext(ioDispatcher) {
            quotesDao.insert(SimpleQuoteData(symbol, "", "", "", "", null).asEntity())
        }
    }

    override suspend fun deleteFromWatchList(symbol: String) {
        withContext(ioDispatcher) {
            quotesDao.delete(symbol)
        }
    }

    override suspend fun clearWatchList() {
        withContext(ioDispatcher) {
            quotesDao.deleteAll()
        }
    }

    override fun isSymbolInWatchlist(symbol: String): Flow<Boolean> =
        quotesDao.isInWatchlist(symbol)
}