package com.verdenroz.verdaxmarket.core.data.model

import com.verdenroz.verdaxmarket.core.model.Profile
import com.verdenroz.verdaxmarket.core.network.model.ProfileDto

fun ProfileDto.asExternalModel() = Profile(
    quote = quote?.asExternalModel(),
    similar = similar.asExternalModel(),
    performance = performance?.let { perf ->
        val sector = quote?.sector?.toSector() ?: com.verdenroz.verdaxmarket.core.model.enums.Sector.TECHNOLOGY
        perf.asExternalModel(sector)
    },
    news = news.asExternalModel()
)