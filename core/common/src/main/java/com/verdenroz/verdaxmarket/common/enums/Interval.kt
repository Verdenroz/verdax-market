package com.verdenroz.verdaxmarket.common.enums

/**
 * The available intervals for technical analysis and historical data
 */
enum class Interval(val value: String) {
    ONE_MINUTE("1m"),
    FIVE_MINUTE("5m"),
    FIFTEEN_MINUTE("15m"),
    THIRTY_MINUTE("30m"),
    ONE_HOUR("1h"),
    DAILY("1d"),
    WEEKLY("1wk"),
    MONTHLY("1mo"),
    QUARTERLY("3mo")
}