package com.verdenroz.verdaxmarket.core.model.enums

enum class MarketStatus {
    OPEN,
    CLOSED,
    PREMARKET,
    AFTER_HOURS,
    EARLY_CLOSE,
}

enum class MarketStatusReason {
    WEEKEND,
    HOLIDAY,
    REGULAR_HOURS,
    PRE_MARKET,
    AFTER_HOURS,
    EARLY_CLOSE,
    OUTSIDE_HOURS
}
