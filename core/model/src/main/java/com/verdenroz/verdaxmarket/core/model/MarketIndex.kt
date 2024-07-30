package com.verdenroz.verdaxmarket.core.model


/**
 * Local data class for stock market indices
 * @param name Name of the index
 * @param value Current value of the index
 * @param change Change in value the index
 * @param percentChange Percent change in value of the index
 */
data class MarketIndex(
    val name: String,
    val value: String,
    val change: String,
    val percentChange: String
)