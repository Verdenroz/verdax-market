package com.verdenroz.verdaxmarket.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class ProfileResponse(
    val quote: FullQuoteResponse? = null,
    val similar: List<SimpleQuoteResponse>,
    val performance: SectorResponse? = null,
    val news: List<NewsResponse>
)
