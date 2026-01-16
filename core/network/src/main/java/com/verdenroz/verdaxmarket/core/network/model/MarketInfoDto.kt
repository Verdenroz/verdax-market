package com.verdenroz.verdaxmarket.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class MarketInfoDto(
    val actives: List<ScreenerQuoteDto>,
    val gainers: List<ScreenerQuoteDto>,
    val losers: List<ScreenerQuoteDto>,
    val indices: List<IndexDto>,
    val headlines: List<NewsArticleDto>,
    val sectors: List<SectorDetailDto>
)
