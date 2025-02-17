package com.verdenroz.verdaxmarket.core.common.enums

/**
 * The available time periods for technical analysis and historical data
 */
enum class TimePeriod(val value: String) {
    ONE_DAY("1d"),
    FIVE_DAY("5d"),
    ONE_MONTH("1mo"),
    SIX_MONTH("6mo"),
    YEAR_TO_DATE("ytd"),
    ONE_YEAR("1y"),
    FIVE_YEAR("5y"),
}