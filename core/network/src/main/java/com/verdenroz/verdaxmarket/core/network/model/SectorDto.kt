package com.verdenroz.verdaxmarket.core.network.model

import com.verdenroz.verdaxmarket.core.network.NullableDoubleSerializer
import com.verdenroz.verdaxmarket.core.network.NullableLongSerializer
import kotlinx.serialization.Serializable

/**
 * DTO for detailed sector information from the v2 API.
 * Used by /v2/sectors/performance or /v2/sectors/{sector}
 *
 * @param name The name of the sector
 * @param slug The URL-friendly slug for the sector
 * @param overview Overview information about the sector
 * @param performance Performance metrics for the sector
 * @param topCompanies List of top companies in the sector
 * @param etfs List of ETFs related to the sector
 * @param industries List of industries within the sector
 */
@Serializable
data class SectorDetailDto(
    val name: String? = null,
    val slug: String? = null,
    val overview: SectorOverviewDto? = null,
    val performance: SectorPerformanceDto? = null,
    val topCompanies: List<SectorCompanyDto>? = null,
    val etfs: List<SectorEtfDto>? = null,
    val industries: List<SectorIndustryDto>? = null,
)

/**
 * DTO for sector overview information.
 *
 * @param description A description of the sector
 * @param companiesCount The number of companies in the sector
 * @param marketWeight The market weight of the sector as a percentage
 */
@Serializable
data class SectorOverviewDto(
    val description: String? = null,
    val companiesCount: Int? = null,
    @Serializable(with = NullableDoubleSerializer::class)
    val marketWeight: Double? = null,
)

/**
 * DTO for sector performance metrics.
 *
 * @param dayChangePercent The change percentage for the day
 * @param ytdChangePercent The year-to-date change percentage
 * @param oneYearChangePercent The one-year change percentage
 * @param threeYearChangePercent The three-year change percentage
 * @param fiveYearChangePercent The five-year change percentage
 */
@Serializable
data class SectorPerformanceDto(
    @Serializable(with = NullableDoubleSerializer::class)
    val dayChangePercent: Double? = null,
    @Serializable(with = NullableDoubleSerializer::class)
    val ytdChangePercent: Double? = null,
    @Serializable(with = NullableDoubleSerializer::class)
    val oneYearChangePercent: Double? = null,
    @Serializable(with = NullableDoubleSerializer::class)
    val threeYearChangePercent: Double? = null,
    @Serializable(with = NullableDoubleSerializer::class)
    val fiveYearChangePercent: Double? = null,
)

/**
 * DTO for a company within a sector.
 *
 * @param symbol The stock symbol
 * @param name The company name
 * @param lastPrice The last traded price
 * @param marketCap The market capitalization
 * @param marketWeight The market weight within the sector
 * @param dayChangePercent The day's change percentage
 * @param ytdReturn The year-to-date return
 */
@Serializable
data class SectorCompanyDto(
    val symbol: String,
    val name: String? = null,
    @Serializable(with = NullableDoubleSerializer::class)
    val lastPrice: Double? = null,
    @Serializable(with = NullableLongSerializer::class)
    val marketCap: Long? = null,
    @Serializable(with = NullableDoubleSerializer::class)
    val marketWeight: Double? = null,
    @Serializable(with = NullableDoubleSerializer::class)
    val dayChangePercent: Double? = null,
    @Serializable(with = NullableDoubleSerializer::class)
    val ytdReturn: Double? = null,
)

/**
 * DTO for an ETF related to a sector.
 *
 * @param symbol The ETF symbol
 * @param name The ETF name
 * @param lastPrice The last traded price
 * @param dayChangePercent The day's change percentage
 * @param ytdReturn The year-to-date return
 */
@Serializable
data class SectorEtfDto(
    val symbol: String,
    val name: String? = null,
    @Serializable(with = NullableDoubleSerializer::class)
    val lastPrice: Double? = null,
    @Serializable(with = NullableDoubleSerializer::class)
    val dayChangePercent: Double? = null,
    @Serializable(with = NullableDoubleSerializer::class)
    val ytdReturn: Double? = null,
)

/**
 * DTO for an industry within a sector.
 *
 * @param name The industry name
 * @param companiesCount The number of companies in the industry
 * @param marketWeight The market weight of the industry within the sector
 * @param dayChangePercent The day's change percentage
 * @param ytdChangePercent The year-to-date change percentage
 */
@Serializable
data class SectorIndustryDto(
    val name: String,
    val companiesCount: Int? = null,
    @Serializable(with = NullableDoubleSerializer::class)
    val marketWeight: Double? = null,
    @Serializable(with = NullableDoubleSerializer::class)
    val dayChangePercent: Double? = null,
    @Serializable(with = NullableDoubleSerializer::class)
    val ytdChangePercent: Double? = null,
)
