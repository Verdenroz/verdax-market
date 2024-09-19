package com.verdenroz.verdaxmarket.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.verdenroz.verdaxmarket.core.model.RecentSearchQuery
import kotlinx.datetime.Instant

@Entity(tableName = "recentSearches")
data class RecentSearchEntity(
    @PrimaryKey val query: String,
    @ColumnInfo val timestamp: Instant
)

fun RecentSearchEntity.asExternalModel() = RecentSearchQuery(
    query = query,
    timestamp = timestamp,
)
