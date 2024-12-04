package com.verdenroz.verdaxmarket.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

@Entity(tableName = "recentSearches")
data class RecentSearchEntity(
    @PrimaryKey val searchQuery: String,
    @ColumnInfo val timestamp: Instant = Clock.System.now(),
)
