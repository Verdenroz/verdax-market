package com.verdenroz.verdaxmarket.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.verdenroz.verdaxmarket.core.database.model.RecentQuoteEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the [RecentQuoteEntity].
 */
@Dao
interface RecentQuoteDao {

    @Query("SELECT * FROM recentQuotes ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentQuotes(limit: Int = 10): Flow<List<RecentQuoteEntity>>

    @Query("SELECT COUNT(*) FROM recentQuotes")
    fun getRecentQuotesCountFlow(): Flow<Int>

    @Query("DELETE FROM recentQuotes WHERE timestamp = (SELECT MIN(timestamp) FROM recentQuotes)")
    fun deleteOldestRecentQuote()

    @Upsert
    suspend fun upsertRecentQuote(recentQuote: RecentQuoteEntity)

    @Query("DELETE FROM recentQuotes WHERE symbol = :symbol")
    suspend fun deleteRecentQuote(symbol: String)

    @Query("DELETE FROM recentQuotes")
    suspend fun deleteAllRecentQuotes()

}