package com.verdenroz.verdaxmarket.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.verdenroz.verdaxmarket.model.SimpleQuoteData

/**
 * Data class for [SimpleQuoteData]
 * Represents individual stock data in a WatchList
 */
@Entity
data class QuoteEntity(
    @PrimaryKey val symbol: String,
    val name: String,
    val price: Double,
    val change: String,
    val percentChange: String,
)

fun QuoteEntity.asExternalModel() = SimpleQuoteData(
    symbol = symbol,
    name = name,
    price = price,
    change = change,
    percentChange = percentChange,
)