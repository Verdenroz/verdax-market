package com.verdenroz.verdaxmarket.core.data.repository

import com.verdenroz.verdaxmarket.core.database.dao.RecentQuoteDao
import com.verdenroz.verdaxmarket.core.database.dao.RecentSearchDao
import com.verdenroz.verdaxmarket.core.database.model.RecentQuoteEntity
import com.verdenroz.verdaxmarket.core.database.model.RecentSearchEntity
import com.verdenroz.verdaxmarket.core.database.model.asExternalModel
import com.verdenroz.verdaxmarket.core.model.SimpleQuoteData
import com.verdenroz.verdaxmarket.core.network.FinanceQueryDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImplRecentSearchRepository @Inject constructor(
    private val recentSearchDao: RecentSearchDao,
    private val recentQuoteDao: RecentQuoteDao,
    private val financeQueryDataSource: FinanceQueryDataSource
) : RecentSearchRepository {

    override fun getRecentSearchQueries(limit: Int) = recentSearchDao.getRecentSearches(limit).map { recentSearchEntities ->
        recentSearchEntities.map { it.asExternalModel() }
    }

    override fun getRecentQuotes(limit: Int) = recentQuoteDao.getRecentQuotes(limit).map { recentQuoteEntities ->
        recentQuoteEntities.map { it.asExternalModel() }
    }

    override fun isQuoteRecent(symbol: String): Flow<Boolean> = recentQuoteDao.isQuoteRecent(symbol)

    override suspend fun upsertRecentQuery(searchQuery: String) {
        recentSearchDao.upsertRecentSearch(
            RecentSearchEntity(
                query = searchQuery,
                timeStamp = Clock.System.now()
            )
        )
    }

    override suspend fun upsertRecentQuote(symbol: String) {
        val quote = financeQueryDataSource.getSimpleQuote(symbol)
        recentQuoteDao.upsertRecentQuote(
            RecentQuoteEntity(
                symbol = quote.symbol,
                name = quote.name,
                price = quote.price,
                change = quote.change,
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
                timestamp = Clock.System.now()
            )
        )
    }

    override suspend fun clearRecentSearches() {
        recentSearchDao.deleteAllRecentSearches()
        recentQuoteDao.deleteAllRecentQuotes()
    }
}