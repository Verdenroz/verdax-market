package com.verdenroz.verdaxmarket.core.data.repository

import com.verdenroz.verdaxmarket.core.common.dispatchers.Dispatcher
import com.verdenroz.verdaxmarket.core.common.dispatchers.FinanceQueryDispatchers
import com.verdenroz.verdaxmarket.core.database.dao.QuoteDao
import com.verdenroz.verdaxmarket.core.database.model.QuoteEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImplWatchlistRepository @Inject constructor(
    private val quotesDao: QuoteDao,
    @Dispatcher(FinanceQueryDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) : WatchlistRepository {

    override val watchlist: Flow<List<QuoteEntity>> = quotesDao.getAllQuoteDataFlow()

    override suspend fun getWatchlist(): List<QuoteEntity> = quotesDao.getAllQuoteData()


    override suspend fun addToWatchList(symbol: String) {
        withContext(ioDispatcher) {
            val currentMaxOrder = quotesDao.getAllQuoteData().maxOfOrNull { it.order } ?: -1
            val nextOrder = currentMaxOrder + 1
            val quote = QuoteEntity(symbol = symbol, order = nextOrder)
            quotesDao.insert(quote)
        }
    }

    override suspend fun deleteFromWatchList(symbol: String) {
        withContext(ioDispatcher) {
            quotesDao.delete(symbol)
            val quotes = quotesDao.getAllQuoteData().sortedBy { it.order }
            quotes.forEachIndexed { index, quote ->
                quotesDao.insert(quote.copy(order = index))
            }
        }
    }

    override suspend fun clearWatchList() {
        withContext(ioDispatcher) {
            quotesDao.deleteAll()
        }
    }

    override suspend fun changeOrder(symbol: String, newOrder: Int) {
        withContext(ioDispatcher) {
            val quotes = quotesDao.getAllQuoteData().sortedBy { it.order }.toMutableList()
            val quoteToMove = quotes.find { it.symbol == symbol } ?: return@withContext

            quotes.remove(quoteToMove)
            quotes.add(newOrder, quoteToMove.copy(order = newOrder))

            quotes.forEachIndexed { index, quote ->
                quotesDao.insert(quote.copy(order = index))
            }
        }
    }

    override fun isSymbolInWatchlist(symbol: String): Flow<Boolean> =
        quotesDao.isInWatchlist(symbol)
}