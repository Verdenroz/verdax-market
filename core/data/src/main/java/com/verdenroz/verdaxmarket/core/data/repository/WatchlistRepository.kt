package com.verdenroz.verdaxmarket.core.data.repository

import com.verdenroz.verdaxmarket.core.common.error.DataError
import com.verdenroz.verdaxmarket.core.common.result.Result
import com.verdenroz.verdaxmarket.core.model.SimpleQuoteData
import kotlinx.coroutines.flow.Flow

interface WatchlistRepository {

    companion object {
        /**
         * The refresh interval for [watchlist] when market is open
         */
        internal const val WATCHLIST_REFRESH_OPEN = 15000L // 15 seconds

        /**
         * The refresh interval for [watchlist] when market is closed
         */
        internal const val WATCHLIST_REFRESH_CLOSED = 300000L // 5 minutes
    }
    /**
     * The current market status of either open or closed
     */
    val isOpen: Flow<Boolean>

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
    suspend fun updateWatchList(quotes: List<SimpleQuoteData>): Result<Unit, DataError.Local>

    /**
     * Update the user's watch list with new stock data by network connection
     * @param symbols the list of symbols to update
     */
    suspend fun updateWatchlist(symbols: List<String>): Result<Unit, DataError.Network>

    /**
     * Add a symbol to the user's watch list by local data
     */
    suspend fun addToWatchList(quote: SimpleQuoteData): Result<Unit, DataError.Local>

    /**
     * Add a symbol from the user's watch list by network
     */
    suspend fun addToWatchList(symbol: String): Result<Unit, DataError.Local>

    /**
     * Delete a symbol from the user's watch list
     */
    suspend fun deleteFromWatchList(symbol: String): Result<Unit, DataError.Local>

    /**
     * Deletes all data from the user's watch list
     */
    suspend fun clearWatchList(): Result<Unit, DataError.Local>

    /**
     * Check if a symbol is in the user's watch list
     */
    fun isSymbolInWatchlist(symbol: String): Flow<Boolean>
}