package com.verdenroz.verdaxmarket.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.verdenroz.verdaxmarket.core.model.RecentSearchQuery
import kotlinx.datetime.Instant

@Entity(tableName = "recent_searches")
data class RecentSearchEntity(
    @PrimaryKey val query: String,
    @ColumnInfo val timeStamp: Instant
)

fun RecentSearchEntity.asExternalModel() = RecentSearchQuery(
    query = query,
    timeStamp = timeStamp,
)
