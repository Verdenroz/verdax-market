package com.verdenroz.verdaxmarket.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.verdenroz.verdaxmarket.core.database.model.RecentSearchEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the [RecentSearchEntity].
 */
@Dao
interface RecentSearchDao {

    @Query("SELECT * FROM recentSearches ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentSearches(limit: Int = 10): Flow<List<RecentSearchEntity>>

    @Upsert
    suspend fun upsertRecentSearch(recentSearch: RecentSearchEntity)

    @Delete
    suspend fun deleteRecentSearch(query: RecentSearchEntity)

    @Query("DELETE FROM recentSearches")
    suspend fun deleteAllRecentSearches()
}