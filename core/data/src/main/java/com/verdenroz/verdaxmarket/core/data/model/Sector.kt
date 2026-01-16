package com.verdenroz.verdaxmarket.core.data.model

import com.verdenroz.verdaxmarket.core.common.enums.SectorType
import com.verdenroz.verdaxmarket.core.model.MarketSector
import com.verdenroz.verdaxmarket.core.model.enums.Sector
import com.verdenroz.verdaxmarket.core.network.model.SectorDetailDto
import com.verdenroz.verdaxmarket.core.network.model.SectorPerformanceDto
import java.text.DecimalFormat

/**
 * Helper function to format a Double percentage to a String with % sign.
 */
private fun Double?.toFormattedPercentageString(): String {
    if (this == null) return "N/A"
    val formatter = DecimalFormat("#,##0.00")
    return "${formatter.format(this)}%"
}

/**
 * Maps v2 SectorDetailDto to MarketSector domain model.
 */
fun SectorDetailDto.asExternalModel() = MarketSector(
    sector = name.toSector(),
    dayReturn = performance?.dayChangePercent.toFormattedPercentageString(),
    ytdReturn = performance?.ytdChangePercent.toFormattedPercentageString(),
    yearReturn = performance?.oneYearChangePercent.toFormattedPercentageString(),
    threeYearReturn = performance?.threeYearChangePercent.toFormattedPercentageString(),
    fiveYearReturn = performance?.fiveYearChangePercent.toFormattedPercentageString(),
)

/**
 * Maps a list of v2 SectorDetailDto to a list of MarketSector domain models.
 */
fun List<SectorDetailDto>.asExternalModel() = map { it.asExternalModel() }

/**
 * Maps v2 SectorPerformanceDto to MarketSector domain model.
 * Requires a sector parameter since SectorPerformanceDto doesn't have a name field.
 * @param sector The sector to associate with this performance data
 */
fun SectorPerformanceDto.asExternalModel(sector: Sector) = MarketSector(
    sector = sector,
    dayReturn = dayChangePercent.toFormattedPercentageString(),
    ytdReturn = ytdChangePercent.toFormattedPercentageString(),
    yearReturn = oneYearChangePercent.toFormattedPercentageString(),
    threeYearReturn = threeYearChangePercent.toFormattedPercentageString(),
    fiveYearReturn = fiveYearChangePercent.toFormattedPercentageString(),
)

/**
 * Converts a sector name string to a Sector enum (for UI display).
 */
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

/**
 * Converts a sector name string directly to a SectorType enum (for API calls).
 */
fun String.toSectorType(): SectorType = when (this) {
    "Basic Materials" -> SectorType.BASIC_MATERIALS
    "Communication Services" -> SectorType.COMMUNICATION_SERVICES
    "Consumer Cyclical" -> SectorType.CONSUMER_CYCLICAL
    "Consumer Defensive" -> SectorType.CONSUMER_DEFENSIVE
    "Energy" -> SectorType.ENERGY
    "Financial Services" -> SectorType.FINANCIAL_SERVICES
    "Healthcare" -> SectorType.HEALTHCARE
    "Industrials" -> SectorType.INDUSTRIALS
    "Real Estate" -> SectorType.REAL_ESTATE
    "Technology" -> SectorType.TECHNOLOGY
    "Utilities" -> SectorType.UTILITIES
    else -> SectorType.TECHNOLOGY
}