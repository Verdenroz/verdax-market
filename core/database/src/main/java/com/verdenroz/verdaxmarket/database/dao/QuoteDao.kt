package com.verdenroz.verdaxmarket.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.verdenroz.verdaxmarket.database.model.QuoteEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the quotes table.
 * Will be used to interact with local WatchList
 */
@Dao
interface QuoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(quoteData: QuoteEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateAll(quotes: List<QuoteEntity>)

    @Query("DELETE FROM QuoteEntity WHERE symbol = :symbol")
    suspend fun delete(symbol: String)

    @Query("DELETE FROM QuoteEntity")
    suspend fun deleteAll()

    @Query("SELECT * FROM QuoteEntity")
    suspend fun getAllQuoteData(): List<QuoteEntity>

    @Query("SELECT * FROM QuoteEntity")
    fun getAllQuoteDataFlow(): Flow<List<QuoteEntity>>

}