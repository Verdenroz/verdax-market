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
     * Updates the user's watchlist with the provided list of [WatchlistQuote]
     * @param watchlist The new watchlist
     */
    suspend fun updateWatchlist(watchlist: List<WatchlistQuote>)

    /**
     * Move a symbol up in the user's watchlist by decreasing its [WatchlistQuote.order]
     */
    suspend fun moveUp(string: String)

    /**
     * Move a symbol down in the user's watchlist by increasing its [WatchlistQuote.order]
     */
    suspend fun moveDown(string: String)

    /**
     * Check if a symbol is in the user's watchlist
     */
    fun isSymbolInWatchlist(symbol: String): Flow<Boolean>
}