package com.verdenroz.verdaxmarket.data.repository

import com.verdenroz.verdaxmarket.common.error.DataError
import com.verdenroz.verdaxmarket.common.result.Result
import com.verdenroz.verdaxmarket.model.SimpleQuoteData
import kotlinx.coroutines.flow.Flow

interface WatchlistRepository {
    /**
     * The current market status of either open or closed
     */
    val marketStatus: Flow<Boolean>

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
}