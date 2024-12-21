package com.verdenroz.verdaxmarket.core.data.repository

import com.verdenroz.verdaxmarket.core.common.dispatchers.Dispatcher
import com.verdenroz.verdaxmarket.core.common.dispatchers.FinanceQueryDispatchers
import com.verdenroz.verdaxmarket.core.common.error.DataError
import com.verdenroz.verdaxmarket.core.common.result.Result
import com.verdenroz.verdaxmarket.core.data.model.asExternalModel
import com.verdenroz.verdaxmarket.core.data.utils.MarketMonitor
import com.verdenroz.verdaxmarket.core.data.utils.handleNetworkException
import com.verdenroz.verdaxmarket.core.database.dao.QuoteDao
import com.verdenroz.verdaxmarket.core.database.model.QuoteEntity
import com.verdenroz.verdaxmarket.core.model.WatchlistQuote
import com.verdenroz.verdaxmarket.core.network.FinanceQueryDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImplWatchlistRepository @Inject constructor(
    private val quotesDao: QuoteDao,
    private val socketRepository: SocketRepository,
    private val api: FinanceQueryDataSource,
    private val marketMonitor: MarketMonitor,
    @Dispatcher(FinanceQueryDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) : WatchlistRepository {

    companion object {
        private const val MARKET_DATA_REFRESH_OPEN = 15000L // 15 seconds
        private const val MARKET_DATA_REFRESH_CLOSED = 300000L // 5 minutes
    }

    override val watchlist: Flow<List<WatchlistQuote>> = quotesDao.getAllQuoteDataFlow().map { quotes ->
        quotes.map { it.asExternalModel() }
    }

    private val _quotes = MutableSharedFlow<Result<List<WatchlistQuote>, DataError.Network>>()
    override val quotes: SharedFlow<Result<List<WatchlistQuote>, DataError.Network>> =
        _quotes
            .onStart { startQuotesStream() }
            .shareIn(
                CoroutineScope(ioDispatcher + SupervisorJob()),
                SharingStarted.Eagerly,
                replay = 1
            )

    private fun startQuotesStream() {
        val scope = CoroutineScope(ioDispatcher + SupervisorJob())
        scope.launch {
            quotesDao.getAllQuoteDataFlow()
                .map { quotes -> quotes.map { it.symbol to it.order }.toMap() }
                .distinctUntilChanged { oldSymbols, newSymbols ->
                    oldSymbols.size == newSymbols.size && oldSymbols.keys.containsAll(newSymbols.keys)
                }
                .flatMapLatest { symbolsWithOrderMap ->
                    val symbols = symbolsWithOrderMap.keys.toList()
                    if (symbols.isEmpty()) {
                        flowOf(Result.Success(emptyList()))
                    } else {
                        // Get the watchlist quotes from the socket if available
                        socketRepository.getQuotes(symbols).flatMapLatest { socketResult ->
                            when (socketResult) {
                                is Result.Success -> {
                                    val quotesWithOrder = socketResult.data.map { quote ->
                                        val order = symbolsWithOrderMap[quote.symbol] ?: 0
                                        WatchlistQuote(
                                            symbol = quote.symbol,
                                            name = quote.name,
                                            price = quote.price,
                                            change = quote.change,
                                            percentChange = quote.percentChange,
                                            logo = quote.logo,
                                            order = order
                                        )
                                    }
                                    flowOf(Result.Success(quotesWithOrder))
                                }

                                is Result.Loading -> flowOf(Result.Loading())
                                // If the socket is not available, poll the quotes from the API
                                else -> marketMonitor.isMarketOpen.flatMapLatest { isOpen ->
                                    flow {
                                        while (true) {
                                            val updatedQuotes =
                                                api.getBulkQuote(symbols).asExternalModel()
                                                    .map { quote ->
                                                        val order =
                                                            symbolsWithOrderMap[quote.symbol] ?: 0
                                                        WatchlistQuote(
                                                            symbol = quote.symbol,
                                                            name = quote.name,
                                                            price = quote.price,
                                                            change = quote.change,
                                                            percentChange = quote.percentChange,
                                                            logo = quote.logo,
                                                            order = order
                                                        )
                                                    }
                                            emit(Result.Success(updatedQuotes))

                                            when (isOpen) {
                                                true -> delay(MARKET_DATA_REFRESH_OPEN) // 15 seconds
                                                false -> delay(MARKET_DATA_REFRESH_CLOSED) // 5 minutes
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                .catch { e -> emit(Result.Error(handleNetworkException(e))) }
                .collect { result ->
                    _quotes.emit(result)
                }
        }
    }

    override suspend fun getWatchlist(): List<QuoteEntity> = quotesDao.getAllQuoteData()


    override suspend fun addToWatchList(symbol: String, name: String, logo: String?) {
        withContext(ioDispatcher) {
            val currentMaxOrder = quotesDao.getAllQuoteData().maxOfOrNull { it.order } ?: -1
            val nextOrder = currentMaxOrder + 1
            val quote = QuoteEntity(
                symbol = symbol,
                name = name,
                price = null,
                change = null,
                percentChange = null,
                logo = logo,
                order = nextOrder,
            )
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

    override suspend fun updateWatchlist(watchlist: List<WatchlistQuote>) {
        withContext(ioDispatcher) {
            val currentQuotes = quotesDao.getAllQuoteData()
            val updatedSymbols = watchlist.map { it.symbol }.toSet()
            val quotesToDelete = currentQuotes.filter { it.symbol !in updatedSymbols }

            // Delete quotes that are not in the new watchlist
            quotesDao.deleteAllBySymbols(quotesToDelete.map { it.symbol })

            // Update order of the remaining quotes
            val quotesToUpdate = watchlist.mapIndexed { index, quote ->
                QuoteEntity(
                    symbol = quote.symbol,
                    name = quote.name,
                    price = quote.price,
                    change = quote.change,
                    percentChange = quote.percentChange,
                    logo = quote.logo,
                    order = index
                )
            }
            quotesDao.updateAll(quotesToUpdate)
        }
    }

    override suspend fun moveUp(symbol: String) {
        withContext(ioDispatcher) {
            val quotes = quotesDao.getAllQuoteData().sortedBy { it.order }
            val index = quotes.indexOfFirst { it.symbol == symbol }

            if (index > 0) {
                // Get the quote we want to move and its current order
                val currentQuote = quotes[index]
                val newOrder = currentQuote.order - 1

                // Update all quotes between the new position and the current position
                val quotesToUpdate = quotes.map { quote ->
                    when {
                        quote.symbol == symbol -> quote.copy(order = newOrder)
                        quote.order == newOrder -> quote.copy(order = quote.order + 1)
                        else -> quote
                    }
                }

                quotesDao.updateAll(quotesToUpdate)
            }
        }
    }

    override suspend fun moveDown(symbol: String) {
        withContext(ioDispatcher) {
            val quotes = quotesDao.getAllQuoteData().sortedBy { it.order }
            val index = quotes.indexOfFirst { it.symbol == symbol }

            if (index < quotes.size - 1) {
                // Get the quote we want to move and its current order
                val currentQuote = quotes[index]
                val newOrder = currentQuote.order + 1

                // Update all quotes between the current position and the new position
                val quotesToUpdate = quotes.map { quote ->
                    when {
                        quote.symbol == symbol -> quote.copy(order = newOrder)
                        quote.order == newOrder -> quote.copy(order = quote.order - 1)
                        else -> quote
                    }
                }

                quotesDao.updateAll(quotesToUpdate)
            }
        }
    }

    override fun isSymbolInWatchlist(symbol: String): Flow<Boolean> =
        quotesDao.isInWatchlist(symbol)
}