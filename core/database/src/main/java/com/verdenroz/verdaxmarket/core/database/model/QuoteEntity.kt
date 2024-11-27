package com.verdenroz.verdaxmarket.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents individual stock data in a WatchList
 * @param symbol the quote symbol
 * @param order the order in the watchlist
 */
@Entity(tableName = "quotes")
data class QuoteEntity(
    @PrimaryKey val symbol: String,
    val order: Int,
)

