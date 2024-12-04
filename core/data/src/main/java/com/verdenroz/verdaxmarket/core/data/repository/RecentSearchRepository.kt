package com.verdenroz.verdaxmarket.core.data.repository

import com.verdenroz.verdaxmarket.core.common.error.DataError
import com.verdenroz.verdaxmarket.core.common.result.Result
import com.verdenroz.verdaxmarket.core.model.SimpleQuoteData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

/**
 * Handles the recently searched queries and quotes.
 */
interface RecentSearchRepository {

    /**
     * Flow of the symbols and names of the recent symbols that have been searched.
     */
    val recentSymbolsNames: Flow<List<Triple<String, String, String?>>>

    /**
     * Flow of the recent quotes that have been searched.
     */
    val recentQuotes: SharedFlow<Result<List<SimpleQuoteData>, DataError.Network>>

    /**
     * Get the recent search queries with most recent first up to [limit].
     * @param limit The number of the recent search queries returned.
     */
    fun getRecentSearchQueries(limit: Int): Flow<List<String>>

    /**
     * Upsert the [query] as part of the recent searches.
     */
    suspend fun upsertRecentQuery(query: String)

    /**
     * Delete the [query] from the recent searches.
     */
    suspend fun deleteRecentQuery(query: String)

    /**
     * Upsert the [symbol] to the recent quotes.
     * @param symbol The symbol of the quote.
     * @param name The name of the quote.
     * @param logo The logo of the quote.
     */
    suspend fun upsertRecentQuote(symbol: String, name: String, logo: String?)

    /**
     * Delete the [symbol] from the recent quotes
     */
    suspend fun deleteRecentQuote(symbol: String)

    /**
     * Clear the recent searches.
     */
    suspend fun clearRecentQueries()

    /**
     * Clear the recent quotes.
     */
    suspend fun clearRecentQuotes()
}