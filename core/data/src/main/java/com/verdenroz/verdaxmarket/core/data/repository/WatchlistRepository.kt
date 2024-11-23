package com.verdenroz.verdaxmarket.core.data.repository

import kotlinx.coroutines.flow.Flow

interface WatchlistRepository {

    /**
     * The user's watch list as a list of symbols
     */
    val watchlist: Flow<List<String>>

    /**
     * Add a symbol to the user's watch list
     */
    suspend fun addToWatchList(symbol: String)

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