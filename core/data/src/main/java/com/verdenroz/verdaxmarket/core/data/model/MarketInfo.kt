package com.verdenroz.verdaxmarket.core.data.model

import com.verdenroz.verdaxmarket.core.common.enums.slugToDisplayName
import com.verdenroz.verdaxmarket.core.model.MarketInfo
import com.verdenroz.verdaxmarket.core.network.model.MarketInfoDto

fun MarketInfoDto.asExternalModel() = MarketInfo(
    actives = actives.asExternalModel(),
    gainers = gainers.asExternalModel(),
    losers = losers.asExternalModel(),
    indices = indices.asExternalModel(),
    headlines = headlines.asExternalModel(),
    sectors = sectors.map { sector ->
        // Populate name from slug if missing
        if (sector.name == null && sector.slug != null) {
            sector.copy(name = slugToDisplayName(sector.slug))
        } else {
            sector
        }
    }.asExternalModel()
)