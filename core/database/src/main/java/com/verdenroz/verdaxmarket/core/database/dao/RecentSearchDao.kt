package com.verdenroz.verdaxmarket.core.database.dao

import androidx.room.Dao
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

    @Query("SELECT COUNT(*) FROM recentSearches")
    fun getRecentSearchesCountFlow(): Flow<Int>

    @Query("DELETE FROM recentSearches WHERE timestamp = (SELECT MIN(timestamp) FROM recentSearches)")
    fun deleteOldestRecentSearch()

    @Upsert
    suspend fun upsertRecentSearch(recentSearch: RecentSearchEntity)

    @Query("DELETE FROM recentSearches WHERE searchQuery = :query")
    suspend fun deleteRecentSearch(query: String)

    @Query("DELETE FROM recentSearches")
    suspend fun deleteAllRecentSearches()
}