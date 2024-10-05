package com.verdenroz.verdaxmarket.core.data.model

import com.verdenroz.verdaxmarket.core.model.Profile
import com.verdenroz.verdaxmarket.core.network.model.ProfileResponse

fun ProfileResponse.asExternalModel() = Profile(
    quote = quote.asExternalModel(),
    similar = similar.asExternalModel(),
    performance = performance?.asExternalModel(),
    news = news.asExternalModel()
)