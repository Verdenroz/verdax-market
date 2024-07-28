package com.verdenroz.verdaxmarket.model

/**
 * Data class for Algolia search results
 * @param symbol the symbol of the security
 * @param name the name of the security
 * @param exchangeShortName the short name of the exchange
 * @param exchange the full name of the exchange
 * @param type the type of security (stock, etf, trust)
 */
data class SearchResult(
    val symbol: String,
    val name: String,
    val exchangeShortName: String,
    val exchange: String,
    val type: String,
)
