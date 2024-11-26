package com.verdenroz.verdaxmarket.core.data.repository

import com.verdenroz.verdaxmarket.core.model.SimpleQuoteData
import kotlinx.coroutines.flow.Flow

interface WatchlistRepository {

    /**
     * The user's watch list as a list of [SimpleQuoteData] periodically updated
     */
    val watchlist: Flow<List<SimpleQuoteData>>

    /**
     * Returns the user's watch list as a list of [SimpleQuoteData] from local data
     */
    suspend fun getWatchlist(): Flow<List<SimpleQuoteData>>

    /**
     * Update the user's watch list with new stock data by local data
     * @param quotes the list of [SimpleQuoteData] to upsert
     */
    suspend fun updateWatchList(quotes: List<SimpleQuoteData>)

    /**
     * Update the user's watch list with new stock data by network connection
     * @param symbols the list of symbols to update
     */
    suspend fun updateWatchlist(symbols: List<String>)

    /**
     * Add a symbol to the user's watch list by local data
     */
    suspend fun addToWatchList(quote: SimpleQuoteData)

    /**
     * Add a symbol from the user's watch list by network
     */
    suspend fun addToWatchList(symbol: String)

    /**
     * Add a placeholder to the user's watch list for an optimistic UI update
     */
    suspend fun addPlaceholderToWatchList(symbol: String)

    /**
     * Delete a symbol from the user's watch list
     */
    suspend fun deleteFromWatchList(symbol: String)

    /**
     * Deletes all data from the user's watch list
     */
    suspend fun clearWatchList()

    /**
     * Check if a symbol is in the user's watch list
     */
    fun isSymbolInWatchlist(symbol: String): Flow<Boolean>
}