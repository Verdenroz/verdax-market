package com.verdenroz.verdaxmarket.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

@Entity(tableName = "recentQuotes")
data class RecentQuoteEntity(
    @PrimaryKey val symbol: String,
    val name: String,
    val logo: String? = null,
    @ColumnInfo val timestamp: Instant = Clock.System.now()
)
