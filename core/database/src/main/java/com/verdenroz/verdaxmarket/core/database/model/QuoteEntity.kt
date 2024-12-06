package com.verdenroz.verdaxmarket.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents individual stock data in a WatchList
 * @param symbol the quote symbol
 * @param order the order in the watchlist
 * @param name the name of the quote
 * @param logo the logo of the quote if available
 */
@Entity(tableName = "quotes")
data class QuoteEntity(
    @PrimaryKey val symbol: String,
    val name: String,
    val price: String?,
    val change: String?,
    val percentChange: String?,
    val logo: String?,
    val order: Int,
)

