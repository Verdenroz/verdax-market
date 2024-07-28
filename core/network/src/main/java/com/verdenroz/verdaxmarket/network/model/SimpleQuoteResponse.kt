package com.verdenroz.verdaxmarket.network.model

import kotlinx.serialization.Serializable

/**
 * Remote data class for smaller individual stock information given by FinanceQuery
 * @param symbol the stock symbol
 * @param name the stock name
 * @param price the stock price
 * @param change the price change
 * @param percentChange the percentage change
 */
@Serializable
data class SimpleQuoteResponse(
    val symbol: String,
    val name: String,
    val price: Double,
    val change: String,
    val percentChange: String,
)
