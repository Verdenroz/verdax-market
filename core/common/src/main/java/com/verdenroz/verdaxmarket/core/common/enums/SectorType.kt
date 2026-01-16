package com.verdenroz.verdaxmarket.core.common.enums

/**
 * The 11 GICS (Global Industry Classification Standard) sectors
 */
enum class SectorType(val value: String) {
    TECHNOLOGY("technology"),
    FINANCIAL_SERVICES("financial-services"),
    CONSUMER_CYCLICAL("consumer-cyclical"),
    COMMUNICATION_SERVICES("communication-services"),
    HEALTHCARE("healthcare"),
    INDUSTRIALS("industrials"),
    CONSUMER_DEFENSIVE("consumer-defensive"),
    ENERGY("energy"),
    BASIC_MATERIALS("basic-materials"),
    REAL_ESTATE("real-estate"),
    UTILITIES("utilities")
}

/**
 * Converts SectorType enum to display name string used by the API and UI.
 */
fun SectorType.toDisplayName(): String = when (this) {
    SectorType.BASIC_MATERIALS -> "Basic Materials"
    SectorType.COMMUNICATION_SERVICES -> "Communication Services"
    SectorType.CONSUMER_CYCLICAL -> "Consumer Cyclical"
    SectorType.CONSUMER_DEFENSIVE -> "Consumer Defensive"
    SectorType.ENERGY -> "Energy"
    SectorType.FINANCIAL_SERVICES -> "Financial Services"
    SectorType.HEALTHCARE -> "Healthcare"
    SectorType.INDUSTRIALS -> "Industrials"
    SectorType.REAL_ESTATE -> "Real Estate"
    SectorType.TECHNOLOGY -> "Technology"
    SectorType.UTILITIES -> "Utilities"
}

/**
 * Converts a sector slug (URL-friendly identifier) to display name.
 * Used when API returns sectors without names but with slugs.
 */
fun slugToDisplayName(slug: String?): String = when (slug) {
    "basic-materials" -> "Basic Materials"
    "communication-services" -> "Communication Services"
    "consumer-cyclical" -> "Consumer Cyclical"
    "consumer-defensive" -> "Consumer Defensive"
    "energy" -> "Energy"
    "financial-services" -> "Financial Services"
    "healthcare" -> "Healthcare"
    "industrials" -> "Industrials"
    "real-estate" -> "Real Estate"
    "technology" -> "Technology"
    "utilities" -> "Utilities"
    else -> "Technology" // Fallback
}
