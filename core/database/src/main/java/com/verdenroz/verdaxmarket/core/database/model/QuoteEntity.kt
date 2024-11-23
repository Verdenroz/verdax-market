package com.verdenroz.verdaxmarket.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents individual symbols in the watchlist to later be used to fetch data from the API
 */
@Entity(tableName = "quotes")
data class QuoteEntity(
    @PrimaryKey val symbol: String,
)
