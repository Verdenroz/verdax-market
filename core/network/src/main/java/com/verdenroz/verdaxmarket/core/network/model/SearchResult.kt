package com.verdenroz.verdaxmarket.core.network.model

import kotlinx.serialization.Serializable

/**
 * Data class for Algolia search results
 * @param symbol the symbol of the security
 * @param name the name of the security
 * @param exchangeShortName the short name of the exchange
 * @param exchange the full name of the exchange
 * @param type the type of security (stock, etf, trust)
 * @param objectID the object ID of the security (used by Algolia)
 */
@Serializable
data class SearchResult(
    val symbol: String,
    val name: String,
    val exchangeShortName: String,
    val exchange: String,
    val type: String,
    val objectID: String,
)