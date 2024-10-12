package com.verdenroz.verdaxmarket.core.data.repository

import com.verdenroz.verdaxmarket.core.common.dispatchers.Dispatcher
import com.verdenroz.verdaxmarket.core.common.dispatchers.FinanceQueryDispatchers
import com.verdenroz.verdaxmarket.core.data.model.asEntity
import com.verdenroz.verdaxmarket.core.data.utils.MarketStatusMonitor
import com.verdenroz.verdaxmarket.core.data.utils.handleLocalException
import com.verdenroz.verdaxmarket.core.data.utils.handleNetworkException
import com.verdenroz.verdaxmarket.core.database.dao.QuoteDao
import com.verdenroz.verdaxmarket.core.database.model.asExternalModel
import com.verdenroz.verdaxmarket.core.model.SimpleQuoteData
import com.verdenroz.verdaxmarket.core.network.FinanceQueryDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
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

    private val _watchlist = MutableStateFlow<List<SimpleQuoteData>>(emptyList())
    override val watchlist: StateFlow<List<SimpleQuoteData>> = _watchlist.asStateFlow()

    init {
        CoroutineScope(ioDispatcher).launch {
            quotesDao.getAllQuoteDataFlow().map { it.asExternalModel() }
                .collect { _watchlist.value = it }
        }
    }

    override val isOpen: StateFlow<Boolean> = marketStatusMonitor.isMarketOpen.stateIn(
        CoroutineScope(ioDispatcher),
        SharingStarted.WhileSubscribed(5000L),
        marketStatusMonitor.isMarketOpen()
    )

    override suspend fun getWatchlist(): Flow<List<SimpleQuoteData>> =
        quotesDao.getAllQuoteDataFlow().map { it.asExternalModel() }

    override suspend fun updateWatchList(quotes: List<SimpleQuoteData>) {
        return withContext(ioDispatcher) {
            try {
                val updatedQuotes = quotes.map { it.asEntity() }
                quotesDao.updateAll(updatedQuotes)
                _watchlist.value = updatedQuotes.asExternalModel()
            } catch (e: Exception) {
                handleLocalException(e)
            }
        }
    }

    override suspend fun updateWatchlist(symbols: List<String>) {
        return withContext(ioDispatcher) {
            try {
                val quotes = api.getBulkQuote(symbols).asEntity()
                quotesDao.updateAll(quotes)
                _watchlist.value = quotes.asExternalModel()
            } catch (e: Exception) {
                handleNetworkException(e)
            }
        }
    }

    override suspend fun addToWatchList(quote: SimpleQuoteData) {
        return withContext(ioDispatcher) {
            try {
                quotesDao.insert(quote.asEntity())
                _watchlist.value += quote
            } catch (e: Exception) {
                handleLocalException(e)
            }
        }
    }

    override suspend fun addToWatchList(symbol: String){
        return withContext(ioDispatcher) {
            try {
                val quote = api.getSimpleQuote(symbol).asEntity()
                quotesDao.insert(quote)
                _watchlist.value += quote.asExternalModel()
            } catch (e: Exception) {
                handleLocalException(e)
            }
        }
    }

    override suspend fun deleteFromWatchList(symbol: String) {
        return withContext(ioDispatcher) {
            try {
                quotesDao.delete(symbol)
                _watchlist.value = _watchlist.value.filterNot { it.symbol == symbol }
            } catch (e: Exception) {
                handleLocalException(e)
            }
        }
    }

    override suspend fun clearWatchList() {
        return withContext(ioDispatcher) {
            try {
                quotesDao.deleteAll()
                _watchlist.value = emptyList()
            } catch (e: Exception) {
                handleLocalException(e)
            }
        }
    }

    override fun isSymbolInWatchlist(symbol: String): Flow<Boolean> =
        quotesDao.isInWatchlist(symbol)
}