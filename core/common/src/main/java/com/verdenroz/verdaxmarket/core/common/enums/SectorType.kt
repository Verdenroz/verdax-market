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
