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
        internal const val WATCHLIST_REFRESH_OPEN = 30000L // 30 seconds

        /**
         * The refresh interval for [watchlist] when market is closed
         */
        internal const val WATCHLIST_REFRESH_CLOSED = 600000L // 10 minutes
    }
    /**
     * The current market status of either open or closed
     */
    val isOpen: Flow<Boolean>

    /**
     * The user's watch list as a list of [SimpleQuoteData]
     */
    val watchlist: Flow<Result<List<SimpleQuoteData>, DataError.Local>>

    /**
     * Refresh the user's watch list with new stock data
     */
    suspend fun refreshWatchList(): Result<Unit, DataError.Local>

    /**
     * Add a symbol to the user's watch list
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