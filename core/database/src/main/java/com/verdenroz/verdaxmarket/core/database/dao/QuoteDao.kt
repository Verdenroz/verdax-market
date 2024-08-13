package com.verdenroz.verdaxmarket.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.verdenroz.verdaxmarket.core.database.model.QuoteEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the quotes table.
 * Will be used to interact with local WatchList
 */
@Dao
interface QuoteDao {
    @Upsert
    suspend fun insert(quoteData: QuoteEntity)

    @Upsert
    suspend fun updateAll(quotes: List<QuoteEntity>)

    @Query("DELETE FROM quotes WHERE symbol = :symbol")
    suspend fun delete(symbol: String)

    @Query("DELETE FROM quotes")
    suspend fun deleteAll()

    @Query("SELECT * FROM quotes")
    suspend fun getAllQuoteData(): List<QuoteEntity>

    @Query("SELECT * FROM quotes")
    fun getAllQuoteDataFlow(): Flow<List<QuoteEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM quotes WHERE symbol = :symbol)")
    fun isInWatchlist(symbol: String): Flow<Boolean>

}