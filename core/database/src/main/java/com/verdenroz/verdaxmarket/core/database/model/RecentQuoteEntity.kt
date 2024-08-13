package com.verdenroz.verdaxmarket.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.verdenroz.verdaxmarket.core.model.RecentQuoteResult
import kotlinx.datetime.Instant

@Entity(tableName = "recentQuotes")
data class RecentQuoteEntity(
    @PrimaryKey val symbol: String,
    val name: String,
    val price: Double,
    val change: String,
    @ColumnInfo val timestamp: Instant
)

fun RecentQuoteEntity.asExternalModel() = RecentQuoteResult(
    symbol = symbol,
    name = name,
    price = price,
    change = change,
    timestamp = timestamp,
)