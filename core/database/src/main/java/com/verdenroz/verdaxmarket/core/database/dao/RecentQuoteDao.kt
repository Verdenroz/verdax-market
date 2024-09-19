package com.verdenroz.verdaxmarket.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
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

    @Upsert
    suspend fun upsertRecentQuote(recentQuote: RecentQuoteEntity)

    @Delete
    suspend fun deleteRecentQuote(recentQuote: RecentQuoteEntity)

    @Query("DELETE FROM recentQuotes")
    suspend fun deleteAllRecentQuotes()

}