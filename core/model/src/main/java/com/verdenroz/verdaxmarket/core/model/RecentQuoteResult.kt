package com.verdenroz.verdaxmarket.core.model

import kotlinx.datetime.Instant

/**
 * Represents a quote that was recently searched or fetched
 * @param symbol The symbol of the quote.
 * @param name The name of the quote.
 * @param price The price of the quote.
 * @param change The change in the quote.
 * @param timestamp The time when the quote was fetched.
 */
data class RecentQuoteResult(
    val symbol: String,
    val name: String,
    val price: String,
    val change: String,
    val percentChange: String,
    val logo: String?,
    val timestamp: Instant,
)
