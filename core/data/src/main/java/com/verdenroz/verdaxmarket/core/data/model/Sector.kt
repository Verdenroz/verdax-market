package com.verdenroz.verdaxmarket.core.data.model

import com.verdenroz.verdaxmarket.core.model.MarketSector
import com.verdenroz.verdaxmarket.core.model.enums.Sector
import com.verdenroz.verdaxmarket.core.network.model.SectorResponse

fun SectorResponse.asExternalModel() = MarketSector(
    sector = sector.toSector(),
    dayReturn = dayReturn,
    ytdReturn = ytdReturn,
    yearReturn = yearReturn,
    threeYearReturn = threeYearReturn,
    fiveYearReturn = fiveYearReturn,
)

fun List<SectorResponse>.asExternalModel() = map { it.asExternalModel() }

fun String.toSector(): Sector = when (this) {
    "Basic Materials" -> Sector.BASIC_MATERIALS
    "Communication Services" -> Sector.COMMUNICATION_SERVICES
    "Consumer Cyclical" -> Sector.CONSUMER_CYCLICAL
    "Consumer Defensive" -> Sector.CONSUMER_DEFENSIVE
    "Energy" -> Sector.ENERGY
    "Financial Services" -> Sector.FINANCIAL_SERVICES
    "Healthcare" -> Sector.HEALTHCARE
    "Industrials" -> Sector.INDUSTRIALS
    "Real Estate" -> Sector.REAL_ESTATE
    "Technology" -> Sector.TECHNOLOGY
    "Utilities" -> Sector.UTILITIES
    else -> Sector.TECHNOLOGY
}