package com.verdenroz.verdaxmarket.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class ProfileDto(
    val quote: QuoteDto? = null,
    val similar: List<QuoteDto>,
    val performance: SectorPerformanceDto? = null,
    val news: List<NewsArticleDto>
)
