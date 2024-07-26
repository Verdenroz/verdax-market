package com.verdenroz.common.enums

/**
 * The available time periods for technical analysis and historical data
 */
enum class TimePeriod(val value: String) {
    ONE_DAY("1d"),
    FIVE_DAY("5d"),
    ONE_WEEK("7d"),
    ONE_MONTH("1mo"),
    THREE_MONTH("3mo"),
    SIX_MONTH("6mo"),
    YEAR_TO_DATE("YTD"),
    ONE_YEAR("1Y"),
    FIVE_YEAR("5Y"),
    TEN_YEAR("10Y"),
    MAX("max")
}