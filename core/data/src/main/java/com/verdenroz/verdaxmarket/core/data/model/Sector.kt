package com.verdenroz.verdaxmarket.core.data.model

import com.verdenroz.verdaxmarket.core.model.MarketSector
import com.verdenroz.verdaxmarket.core.network.model.SectorResponse

fun SectorResponse.asExternalModel() = MarketSector(
    sector = sector,
    dayReturn = dayReturn,
    ytdReturn = ytdReturn,
    yearReturn = yearReturn,
    threeYearReturn = threeYearReturn,
    fiveYearReturn = fiveYearReturn,
)

fun List<SectorResponse>.asExternalModel() = map { it.asExternalModel() }