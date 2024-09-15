package com.verdenroz.verdaxmarket.core.data.model

import com.verdenroz.verdaxmarket.core.model.MarketInfo
import com.verdenroz.verdaxmarket.core.network.model.MarketInfoResponse

fun MarketInfoResponse.asExternalModel() = MarketInfo(
    actives = actives.asExternalModel(),
    gainers = gainers.asExternalModel(),
    losers = losers.asExternalModel(),
    indices = indices.asExternalModel(),
    headlines = headlines.asExternalModel(),
    sectors = sectors.asExternalModel()
)