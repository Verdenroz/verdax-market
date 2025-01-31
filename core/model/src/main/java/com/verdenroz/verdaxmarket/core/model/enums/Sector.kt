package com.verdenroz.verdaxmarket.core.model.enums

/**
 * Represents a sector of the market
 */
enum class Sector {
    BASIC_MATERIALS,
    COMMUNICATION_SERVICES,
    CONSUMER_CYCLICAL,
    CONSUMER_DEFENSIVE,
    ENERGY,
    FINANCIAL_SERVICES,
    HEALTHCARE,
    INDUSTRIALS,
    REAL_ESTATE,
    TECHNOLOGY,
    UTILITIES
}

fun Sector.toDisplayName(): String {
    return when (this) {
        Sector.BASIC_MATERIALS -> "Basic Materials"
        Sector.COMMUNICATION_SERVICES -> "Communication Services"
        Sector.CONSUMER_CYCLICAL -> "Consumer Cyclical"
        Sector.CONSUMER_DEFENSIVE -> "Consumer Defensive"
        Sector.ENERGY -> "Energy"
        Sector.FINANCIAL_SERVICES -> "Financial Services"
        Sector.HEALTHCARE -> "Healthcare"
        Sector.INDUSTRIALS -> "Industrials"
        Sector.REAL_ESTATE -> "Real Estate"
        Sector.TECHNOLOGY -> "Technology"
        Sector.UTILITIES -> "Utilities"
    }
}

fun Sector.toSymbol(): String {
    return when (this){
        Sector.BASIC_MATERIALS -> "^YH101"
        Sector.COMMUNICATION_SERVICES -> "^YH308"
        Sector.CONSUMER_CYCLICAL -> "^YH102"
        Sector.CONSUMER_DEFENSIVE -> "^YH205"
        Sector.ENERGY -> "^YH309"
        Sector.FINANCIAL_SERVICES -> "^YH103"
        Sector.HEALTHCARE -> "^YH206"
        Sector.INDUSTRIALS -> "^YH310"
        Sector.REAL_ESTATE -> "^YH104"
        Sector.TECHNOLOGY -> "^YH311"
        Sector.UTILITIES -> "^YH207"
    }
}