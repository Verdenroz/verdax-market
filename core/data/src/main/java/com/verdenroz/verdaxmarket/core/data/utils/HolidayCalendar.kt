package com.verdenroz.verdaxmarket.core.data.utils

import java.time.*
import java.time.temporal.TemporalAdjusters

/**
 * Determines US market holidays algorithmically where possible
 */
internal fun isMarketHoliday(date: LocalDate): Boolean {
    return when {
        // New Year's Day - January 1st
        date.isNewYearsDay() -> true

        // Martin Luther King Jr. Day - Third Monday in January
        date.isMLKDay() -> true

        // Presidents Day - Third Monday in February
        date.isPresidentsDay() -> true

        // Good Friday - Requires Easter calculation
        date.isGoodFriday() -> true

        // Memorial Day - Last Monday in May
        date.isMemorialDay() -> true

        // Juneteenth - June 19th
        date.isJuneteenth() -> true

        // Independence Day - July 4th
        date.isIndependenceDay() -> true

        // Labor Day - First Monday in September
        date.isLaborDay() -> true

        // Thanksgiving - Fourth Thursday in November
        date.isThanksgiving() -> true

        // Christmas - December 25th
        date.isChristmas() -> true

        else -> false
    }
}

fun isEarlyCloseDay(date: LocalDate): Boolean {
    return when {
        // Day before Independence Day (July 3rd, unless it's a weekend)
        date.isDayBeforeIndependenceDay() -> true

        // Black Friday (Day after Thanksgiving)
        date.isBlackFriday() -> true

        // Christmas Eve (December 24th, unless it's a weekend)
        date.isChristmasEve() -> true

        else -> false
    }
}

private fun LocalDate.isNewYearsDay(): Boolean {
    val newYearsDay = LocalDate.of(year, Month.JANUARY, 1)
    // If New Year's falls on weekend, observe on closest weekday
    return when {
        this == newYearsDay && !isWeekend() -> true
        this == newYearsDay.plusDays(1) && newYearsDay.dayOfWeek == DayOfWeek.SUNDAY -> true
        this == newYearsDay.minusDays(1) && newYearsDay.dayOfWeek == DayOfWeek.SATURDAY -> true
        else -> false
    }
}

private fun LocalDate.isMLKDay(): Boolean {
    // Third Monday in January
    val mlkDay = LocalDate.of(year, Month.JANUARY, 1)
        .with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY))
        .plusWeeks(2)
    return this == mlkDay
}

private fun LocalDate.isPresidentsDay(): Boolean {
    // Third Monday in February
    val presidentsDay = LocalDate.of(year, Month.FEBRUARY, 1)
        .with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY))
        .plusWeeks(2)
    return this == presidentsDay
}

private fun LocalDate.isGoodFriday(): Boolean {
    // Easter calculation (using Western calendar)
    val easter = calculateEaster(year)
    return this == easter.minusDays(2)
}

private fun LocalDate.isMemorialDay(): Boolean {
    // Last Monday in May
    val memorialDay = LocalDate.of(year, Month.MAY, 1)
        .with(TemporalAdjusters.lastInMonth(DayOfWeek.MONDAY))
    return this == memorialDay
}

private fun LocalDate.isJuneteenth(): Boolean {
    val juneteenth = LocalDate.of(year, Month.JUNE, 19)
    return when {
        this == juneteenth && !isWeekend() -> true
        this == juneteenth.plusDays(1) && juneteenth.dayOfWeek == DayOfWeek.SUNDAY -> true
        this == juneteenth.minusDays(1) && juneteenth.dayOfWeek == DayOfWeek.SATURDAY -> true
        else -> false
    }
}

private fun LocalDate.isIndependenceDay(): Boolean {
    val independenceDay = LocalDate.of(year, Month.JULY, 4)
    return when {
        this == independenceDay && !isWeekend() -> true
        this == independenceDay.plusDays(1) && independenceDay.dayOfWeek == DayOfWeek.SUNDAY -> true
        this == independenceDay.minusDays(1) && independenceDay.dayOfWeek == DayOfWeek.SATURDAY -> true
        else -> false
    }
}

private fun LocalDate.isLaborDay(): Boolean {
    // First Monday in September
    val laborDay = LocalDate.of(year, Month.SEPTEMBER, 1)
        .with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY))
    return this == laborDay
}

private fun LocalDate.isThanksgiving(): Boolean {
    // Fourth Thursday in November
    val thanksgiving = LocalDate.of(year, Month.NOVEMBER, 1)
        .with(TemporalAdjusters.firstInMonth(DayOfWeek.THURSDAY))
        .plusWeeks(3)
    return this == thanksgiving
}

private fun LocalDate.isChristmas(): Boolean {
    val christmas = LocalDate.of(year, Month.DECEMBER, 25)
    return when {
        this == christmas && !isWeekend() -> true
        this == christmas.plusDays(1) && christmas.dayOfWeek == DayOfWeek.SUNDAY -> true
        this == christmas.minusDays(1) && christmas.dayOfWeek == DayOfWeek.SATURDAY -> true
        else -> false
    }
}

private fun LocalDate.isBlackFriday(): Boolean {
    // Day after Thanksgiving
    val thanksgiving = LocalDate.of(year, Month.NOVEMBER, 1)
        .with(TemporalAdjusters.firstInMonth(DayOfWeek.THURSDAY))
        .plusWeeks(3)
    return this == thanksgiving.plusDays(1)
}

private fun LocalDate.isChristmasEve(): Boolean {
    val christmasEve = LocalDate.of(year, Month.DECEMBER, 24)
    return this == christmasEve && !isWeekend()
}

private fun LocalDate.isDayBeforeIndependenceDay(): Boolean {
    val july3rd = LocalDate.of(year, Month.JULY, 3)
    return this == july3rd && !isWeekend()
}

private fun LocalDate.isWeekend(): Boolean {
    return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY
}

// Helper function to calculate Easter Sunday for a given year
private fun calculateEaster(year: Int): LocalDate {
    val a = year % 19
    val b = year / 100
    val c = year % 100
    val d = b / 4
    val e = b % 4
    val f = (b + 8) / 25
    val g = (b - f + 1) / 3
    val h = (19 * a + b - d - g + 15) % 30
    val i = c / 4
    val k = c % 4
    val l = (32 + 2 * e + 2 * i - h - k) % 7
    val m = (a + 11 * h + 22 * l) / 451

    val month = (h + l - 7 * m + 114) / 31
    val day = ((h + l - 7 * m + 114) % 31) + 1

    return LocalDate.of(year, month, day)
}
