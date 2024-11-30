package com.verdenroz.verdaxmarket.core.data.repository

import com.verdenroz.verdaxmarket.core.common.error.DataError
import com.verdenroz.verdaxmarket.core.common.result.Result
import com.verdenroz.verdaxmarket.core.database.model.QuoteEntity
import com.verdenroz.verdaxmarket.core.model.SimpleQuoteData
import kotlinx.coroutines.flow.Flow

interface WatchlistRepository {

    /**
     * The user's watchlist as a flow list of symbols
     */
    val watchlist: Flow<List<QuoteEntity>>

    /**
     * The user's watchlist as a flow of [SimpleQuoteData],
     * fetched from the socket and falling back to polling the API if there is an error
     */
    val quotes: Flow<Result<List<SimpleQuoteData>, DataError.Network>>

    /**
     * Returns the user's watchlist as a list of symbols
     */
    suspend fun getWatchlist(): List<QuoteEntity>

    /**
     * Add a symbol from the user's watchlist
     */
    suspend fun addToWatchList(symbol: String)

    /**
     * Delete a symbol from the user's watchlist
     */
    suspend fun deleteFromWatchList(symbol: String)

    /**
     * Deletes all data from the user's watchlist
     */
    suspend fun clearWatchList()

    /**
     * Change the order of a symbol in the user's watchlist
     */
    suspend fun changeOrder(symbol: String, order: Int)

    /**
     * Check if a symbol is in the user's watchlist
     */
    fun isSymbolInWatchlist(symbol: String): Flow<Boolean>
}