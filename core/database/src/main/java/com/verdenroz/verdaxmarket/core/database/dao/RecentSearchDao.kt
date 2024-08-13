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

    @Query("SELECT * FROM recent_searches ORDER BY timeStamp DESC LIMIT :limit")
    fun getRecentSearches(limit: Int = 10): Flow<List<RecentSearchEntity>>

    @Upsert
    suspend fun upsertRecentSearch(recentSearch: RecentSearchEntity)

    @Query("DELETE FROM recent_searches")
    suspend fun deleteAllRecentSearches()
}