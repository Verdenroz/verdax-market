package com.verdenroz.verdaxmarket.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.verdenroz.verdaxmarket.core.model.SimpleQuoteData

/**
 * Data class for [SimpleQuoteData]
 * Represents individual stock data in a WatchList
 */
@Entity(tableName = "quotes")
data class QuoteEntity(
    @PrimaryKey val symbol: String,
    val name: String,
    val price: Double,
    val change: String,
    val percentChange: String,
    val logo: String?
)

fun QuoteEntity.asExternalModel() = SimpleQuoteData(
    symbol = symbol,
    name = name,
    price = price,
    change = change,
    percentChange = percentChange,
    logo = logo
)

fun List<QuoteEntity>.asExternalModel() = map { it.asExternalModel()}
