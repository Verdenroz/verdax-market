package com.verdenroz.verdaxmarket.core.data.repository

import com.verdenroz.verdaxmarket.core.common.error.DataError
import com.verdenroz.verdaxmarket.core.common.result.Result
import com.verdenroz.verdaxmarket.core.database.model.QuoteEntity
import com.verdenroz.verdaxmarket.core.model.SimpleQuoteData
import com.verdenroz.verdaxmarket.core.model.WatchlistQuote
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

interface WatchlistRepository {

    /**
     * The user's watchlist as a flow list of [QuoteEntity]
     */
    val watchlist: Flow<List<WatchlistQuote>>

    /**
     * The user's watchlist as a flow of [SimpleQuoteData],
     * fetched from the socket and falling back to polling the API if there is an error
     */
    val quotes: SharedFlow<Result<List<WatchlistQuote>, DataError.Network>>

    /**
     * Returns the user's watchlist as a list of symbols
     */
    suspend fun getWatchlist(): List<QuoteEntity>

    /**
     * Add a symbol from the user's watchlist
     */
    suspend fun addToWatchList(symbol: String, name: String, logo: String?)

    /**
     * Delete a symbol from the user's watchlist
     */
    suspend fun deleteFromWatchList(symbol: String)

    /**
     * Deletes all data from the user's watchlist
     */
    suspend fun clearWatchList()

    /**
     * Update the order of the watchlist
     */
    suspend fun updateWatchlistOrder(watchlist: List<WatchlistQuote>)

    /**
     * Check if a symbol is in the user's watchlist
     */
    fun isSymbolInWatchlist(symbol: String): Flow<Boolean>
}