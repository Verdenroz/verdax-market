package com.verdenroz.verdaxmarket.core.data.repository

import com.verdenroz.verdaxmarket.core.common.dispatchers.Dispatcher
import com.verdenroz.verdaxmarket.core.common.dispatchers.FinanceQueryDispatchers
import com.verdenroz.verdaxmarket.core.common.error.DataError
import com.verdenroz.verdaxmarket.core.common.result.Result
import com.verdenroz.verdaxmarket.core.data.model.asExternalModel
import com.verdenroz.verdaxmarket.core.data.utils.handleNetworkException
import com.verdenroz.verdaxmarket.core.database.dao.RecentQuoteDao
import com.verdenroz.verdaxmarket.core.database.dao.RecentSearchDao
import com.verdenroz.verdaxmarket.core.database.model.RecentQuoteEntity
import com.verdenroz.verdaxmarket.core.database.model.RecentSearchEntity
import com.verdenroz.verdaxmarket.core.model.SimpleQuoteData
import com.verdenroz.verdaxmarket.core.network.FinanceQueryDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImplRecentSearchRepository @Inject constructor(
    private val recentSearchDao: RecentSearchDao,
    private val recentQuoteDao: RecentQuoteDao,
    private val financeQueryDataSource: FinanceQueryDataSource,
    @Dispatcher(FinanceQueryDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) : RecentSearchRepository {

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

    override fun getRecentSearchQueries(limit: Int) =
        recentSearchDao.getRecentSearches(limit).map { recentSearchEntities ->
            recentSearchEntities.map { it.asExternalModel() }
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