package com.verdenroz.verdaxmarket.core.data.repository

import com.verdenroz.verdaxmarket.core.common.dispatchers.Dispatcher
import com.verdenroz.verdaxmarket.core.common.dispatchers.FinanceQueryDispatchers
import com.verdenroz.verdaxmarket.core.data.model.asEntity
import com.verdenroz.verdaxmarket.core.database.dao.RecentQuoteDao
import com.verdenroz.verdaxmarket.core.database.dao.RecentSearchDao
import com.verdenroz.verdaxmarket.core.database.model.RecentQuoteEntity
import com.verdenroz.verdaxmarket.core.database.model.RecentSearchEntity
import com.verdenroz.verdaxmarket.core.database.model.asExternalModel
import com.verdenroz.verdaxmarket.core.model.RecentQuoteResult
import com.verdenroz.verdaxmarket.core.model.RecentSearchQuery
import com.verdenroz.verdaxmarket.core.model.SimpleQuoteData
import com.verdenroz.verdaxmarket.core.network.FinanceQueryDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
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
                if (count > 15) {
                    recentSearchDao.deleteOldestRecentSearch()
                }
            }
        }

        // Deletes the oldest recent quote if the count exceeds the limit
        CoroutineScope(ioDispatcher).launch {
            recentQuoteDao.getRecentQuotesCountFlow().collect { count ->
                if (count > 10) {
                    recentQuoteDao.deleteOldestRecentQuote()
                }
            }
        }
    }

    override fun getRecentSearchQueries(limit: Int) =
        recentSearchDao.getRecentSearches(limit).map { recentSearchEntities ->
            recentSearchEntities.map { it.asExternalModel() }
        }

    override fun getRecentQuotes(limit: Int) =
        recentQuoteDao.getRecentQuotes(limit).map { recentQuoteEntities ->
            recentQuoteEntities.map { it.asExternalModel() }
        }

    override suspend fun upsertRecentQuery(searchQuery: String) {
        recentSearchDao.upsertRecentSearch(
            RecentSearchEntity(
                query = searchQuery,
                timestamp = Clock.System.now()
            )
        )
    }

    override suspend fun deleteRecentQuery(searchQuery: RecentSearchQuery) {
        recentSearchDao.deleteRecentSearch(searchQuery.asEntity())
    }

    override suspend fun upsertRecentQuote(symbol: String) {
        val quote = financeQueryDataSource.getSimpleQuote(symbol)
        recentQuoteDao.upsertRecentQuote(
            RecentQuoteEntity(
                symbol = quote.symbol,
                name = quote.name,
                price = quote.price,
                change = quote.change,
                percentChange = quote.percentChange,
                logo = quote.logo,
                timestamp = Clock.System.now()
            )
        )
    }

    override suspend fun upsertRecentQuote(quote: SimpleQuoteData) {
        recentQuoteDao.upsertRecentQuote(
            RecentQuoteEntity(
                symbol = quote.symbol,
                name = quote.name,
                price = quote.price,
                change = quote.change,
                percentChange = quote.percentChange,
                logo = quote.logo,
                timestamp = Clock.System.now()
            )
        )
    }

    override suspend fun deleteRecentQuote(quote: RecentQuoteResult) {
        recentQuoteDao.deleteRecentQuote(quote.asEntity())
    }

    override suspend fun clearRecentQueries() {
        recentSearchDao.deleteAllRecentSearches()
    }

    override suspend fun clearRecentQuotes() {
        recentQuoteDao.deleteAllRecentQuotes()
    }
}