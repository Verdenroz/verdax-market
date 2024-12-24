package com.verdenroz.verdaxmarket.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class HoursInfoResponse(
    val status: String,
    val reason: String,
)
