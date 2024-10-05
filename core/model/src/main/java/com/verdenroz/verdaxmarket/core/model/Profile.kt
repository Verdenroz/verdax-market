package com.verdenroz.verdaxmarket.core.model

/**
 *  Holds aggregated information relevant to a specific symbol.
 *  @param quote The [FullQuoteData] with summary info of the symbol.
 *  @param similar A list of [SimpleQuoteData] that are similar to the symbol.
 *  @param performance The [MarketSector] performance of the symbol.
 *  @param news A list of [News] related to the symbol.
 */
data class Profile(
    val quote: FullQuoteData,
    val similar: List<SimpleQuoteData>,
    val performance: MarketSector?,
    val news: List<News>
)
