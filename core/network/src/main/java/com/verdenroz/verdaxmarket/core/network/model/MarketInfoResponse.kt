package com.verdenroz.verdaxmarket.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class MarketInfoResponse(
    val actives: List<MarketMoverResponse>,
    val gainers: List<MarketMoverResponse>,
    val losers: List<MarketMoverResponse>,
    val indices: List<IndexResponse>,
    val headlines: List<NewsResponse>,
    val sectors: List<SectorResponse>
)