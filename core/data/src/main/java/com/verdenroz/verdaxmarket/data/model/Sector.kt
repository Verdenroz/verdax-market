package com.verdenroz.verdaxmarket.data.model

import com.verdenroz.verdaxmarket.model.MarketSector
import com.verdenroz.verdaxmarket.network.model.SectorResponse

fun SectorResponse.asExternalModel() = MarketSector(
    sector = sector,
    dayReturn = dayReturn,
    ytdReturn = ytdReturn,
    yearReturn = yearReturn,
    threeYearReturn = threeYearReturn,
    fiveYearReturn = fiveYearReturn,
)

fun List<SectorResponse>.asExternalModel() = map { it.asExternalModel() }