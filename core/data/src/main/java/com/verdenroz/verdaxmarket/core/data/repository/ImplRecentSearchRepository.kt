package com.verdenroz.verdaxmarket.core.data.repository

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