package com.verdenroz.verdaxmarket.core.data.repository

import com.verdenroz.verdaxmarket.core.common.dispatchers.Dispatcher
import com.verdenroz.verdaxmarket.core.common.dispatchers.FinanceQueryDispatchers
import com.verdenroz.verdaxmarket.core.common.error.DataError
import com.verdenroz.verdaxmarket.core.common.result.Result
import com.verdenroz.verdaxmarket.core.data.model.asExternalModel
import com.verdenroz.verdaxmarket.core.data.utils.ExceptionHandler
import com.verdenroz.verdaxmarket.core.data.utils.catchAndEmitError
import com.verdenroz.verdaxmarket.core.database.dao.RecentQuoteDao
import com.verdenroz.verdaxmarket.core.database.dao.RecentSearchDao
import com.verdenroz.verdaxmarket.core.database.model.RecentQuoteEntity
import com.verdenroz.verdaxmarket.core.database.model.RecentSearchEntity
import com.verdenroz.verdaxmarket.core.model.SimpleQuoteData
import com.verdenroz.verdaxmarket.core.network.FinanceQueryDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImplRecentSearchRepository @Inject constructor(
    private val recentSearchDao: RecentSearchDao,
    private val recentQuoteDao: RecentQuoteDao,
    private val socketRepository: SocketRepository,
    private val financeQueryDataSource: FinanceQueryDataSource,
    private val exceptionHandler: ExceptionHandler,
    @Dispatcher(FinanceQueryDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) : RecentSearchRepository {

    companion object {
        private const val RECENT_SEARCHES_LIMIT = 15
        private const val RECENT_QUOTES_LIMIT = 10

        private const val POLLING_REFRESH_RATE = 10000L // 10 seconds
    }

    init {
        //D Deletes the oldest recent query if the count exceeds the limit
        CoroutineScope(ioDispatcher).launch {
            recentSearchDao.getRecentSearchesCountFlow().collect { count ->
                if (count > RECENT_SEARCHES_LIMIT) {
                    recentSearchDao.deleteOldestRecentSearch()
                }
            }
        }

        // Deletes the oldest recent quote if the count exceeds the limit
        CoroutineScope(ioDispatcher).launch {
            recentQuoteDao.getRecentQuotesCountFlow().collect { count ->
                if (count > RECENT_QUOTES_LIMIT) {
                    recentQuoteDao.deleteOldestRecentQuote()
                }
            }
        }
    }

    override val recentSymbolsNames: Flow<List<Triple<String, String, String?>>> =
        recentQuoteDao.getRecentQuotes(RECENT_QUOTES_LIMIT).map { quotes ->
            quotes.map { quote -> Triple(quote.symbol, quote.name, quote.logo) }
        }

    private val _recentQuotes = MutableSharedFlow<Result<List<SimpleQuoteData>, DataError.Network>>()
    override val recentQuotes: SharedFlow<Result<List<SimpleQuoteData>, DataError.Network>> =
        _recentQuotes
            .onStart { startRecentQuotesStream() }
            .shareIn(
                CoroutineScope(ioDispatcher + SupervisorJob()),
                SharingStarted.Eagerly,
                replay = 1
            )

    private fun startRecentQuotesStream() {
        val scope = CoroutineScope(ioDispatcher + SupervisorJob())
        scope.launch {
            recentQuoteDao.getRecentQuotes(RECENT_QUOTES_LIMIT)
                .map { it.map { quote -> quote.symbol } }
                .distinctUntilChanged { oldSymbols, newSymbols ->
                    oldSymbols.size == newSymbols.size && oldSymbols.containsAll(newSymbols)
                }
                .flatMapLatest { symbols ->
                    if (symbols.isEmpty()) {
                        flowOf(Result.Success(emptyList()))
                    } else {
                        // Get the recent quotes from the socket if available
                        socketRepository.getQuotes(symbols).flatMapLatest { socketResult ->
                            when (socketResult) {
                                is Result.Success -> flowOf(Result.Success(socketResult.data))
                                is Result.Loading -> flowOf(Result.Loading())
                                // If the socket is not available, poll the quotes from the API
                                else -> flow {
                                    while (true) {
                                        val updatedQuotes =
                                            financeQueryDataSource.getBulkQuote(symbols)
                                                .asExternalModel()
                                        emit(Result.Success(updatedQuotes))
                                        delay(POLLING_REFRESH_RATE) // 10 seconds
                                    }
                                }
                            }
                        }
                    }
                }
                .catchAndEmitError(exceptionHandler)
                .collect { result ->
                    _recentQuotes.emit(result)
                }
        }
    }

    override fun getRecentSearchQueries(limit: Int): Flow<List<String>> =
        recentSearchDao.getRecentSearches(limit).map {
            it.map { search -> search.searchQuery }
        }

    override suspend fun upsertRecentQuery(query: String) {
        recentSearchDao.upsertRecentSearch(
            RecentSearchEntity(
                searchQuery = query
            )
        )
    }

    override suspend fun deleteRecentQuery(query: String) {
        recentSearchDao.deleteRecentSearch(query)
    }

    override suspend fun upsertRecentQuote(symbol: String, name: String, logo: String?) {
        recentQuoteDao.upsertRecentQuote(
            RecentQuoteEntity(
                symbol = symbol,
                name = name,
                logo = logo
            )
        )
    }

    override suspend fun deleteRecentQuote(symbol: String) =
        recentQuoteDao.deleteRecentQuote(symbol)


    override suspend fun clearRecentQueries() = recentSearchDao.deleteAllRecentSearches()

    override suspend fun clearRecentQuotes() = recentQuoteDao.deleteAllRecentQuotes()
}