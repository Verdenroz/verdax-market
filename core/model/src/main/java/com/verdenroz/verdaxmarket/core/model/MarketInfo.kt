package com.verdenroz.verdaxmarket.core.model

/**
 *  Aggregated market information.
 *  @param actives List of active market movers.
 *  @param gainers List of market gainers.
 *  @param losers List of market losers.
 *  @param sectors List of market sectors.
 */
data class MarketInfo(
    val actives: List<MarketMover>,
    val gainers: List<MarketMover>,
    val losers: List<MarketMover>,
    val indices: List<MarketIndex>,
    val headlines: List<News>,
    val sectors: List<MarketSector>
)
