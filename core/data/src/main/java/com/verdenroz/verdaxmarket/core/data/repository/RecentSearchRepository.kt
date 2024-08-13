package com.verdenroz.verdaxmarket.core.data.repository

import com.verdenroz.verdaxmarket.core.model.RecentQuoteResult
import com.verdenroz.verdaxmarket.core.model.RecentSearchQuery
import com.verdenroz.verdaxmarket.core.model.SimpleQuoteData
import kotlinx.coroutines.flow.Flow

/**
 * Handles the recently searched queries and quotes.
 */
interface RecentSearchRepository {

    /**
     * Get the [RecentSearchQuery] up to the number of queries specified as [limit].
     */
    fun getRecentSearchQueries(limit: Int): Flow<List<RecentSearchQuery>>

    /**
     * Get the [RecentQuoteResult] up to the number of queries specified as [limit].
     */
    fun getRecentQuotes(limit: Int): Flow<List<RecentQuoteResult>>

    /**
     * Check if the quote is recently opened.
     */
    fun isQuoteRecent(symbol: String): Flow<Boolean>

    /**
     * Upsert the [searchQuery] as part of the recent searches.
     */
    suspend fun upsertRecentQuery(searchQuery: String)

    /**
     * Upsert the [symbol] as part of the recent searches by network.
     */
    suspend fun upsertRecentQuote(symbol: String)

    /**
     * Upsert the [quote] as part of the recent searches by local.
     */
    suspend fun upsertRecentQuote(quote: SimpleQuoteData)

    /**
     * Clear the recent searches.
     */
    suspend fun clearRecentSearches()
}