package com.verdenroz.verdaxmarket.core.data.model

import com.verdenroz.verdaxmarket.core.common.enums.TimePeriod
import com.verdenroz.verdaxmarket.core.model.HistoricalData
import com.verdenroz.verdaxmarket.core.network.model.HistoricalDataResponse
import java.text.SimpleDateFormat
import java.util.Locale

fun HistoricalDataResponse.asExternalModel() = HistoricalData(
    open = open,
    high = high,
    low = low,
    close = close,
    volume = formatVolume(volume),
)

fun Map<String, HistoricalDataResponse>.asExternalModel(timePeriod: TimePeriod = TimePeriod.YEAR_TO_DATE): Map<String, HistoricalData> =
    map { (date, data) -> formatDate(date, timePeriod) to data.asExternalModel() }.toMap()

/**
 * Helper function to format the date based on the number of days since the earliest date
 * @see formatDateForTime
 * @see formatDateForMonth
 * @see formatDateForYear
 */
private fun formatDate(date: String, timePeriod: TimePeriod): String {
    return when (timePeriod) {
        TimePeriod.ONE_DAY -> formatDateForTime(date)
        TimePeriod.FIVE_DAY, TimePeriod.ONE_MONTH -> formatDateForMonth(date)
        else -> formatDateForYear(date)
    }
}

/**
 * Helper function to format the date for time in the form of "h:mm a"
 * Ex. "Jul 1, 2021 9:30 AM" -> "9:30 AM"
 */
private fun formatDateForTime(date: String): String {
    val formatter = SimpleDateFormat("MMM d, yyyy h:mm a", Locale.US)
    val parsedDate = formatter.parse(date)
    val outputFormatter = SimpleDateFormat("h:mm a", Locale.US)
    return parsedDate?.let { outputFormatter.format(it) } ?: date
}

/**
 * Helper function to format the date for month in the form of "MMM d"
 * Ex. "Jul 1, 2021 9:30 AM" -> "Jul 1"
 */
private fun formatDateForMonth(date: String): String {
    val formatter = SimpleDateFormat("MMM d, yyyy h:mm a", Locale.US)
    val parsedDate = formatter.parse(date)
    val outputFormatter = SimpleDateFormat("MMM d", Locale.US)
    return parsedDate?.let { outputFormatter.format(it) } ?: date
}

/**
 * Helper function to format the date for year in the form of "MMM yyyy"
 * Ex. "Jul 1, 2021 9:30 AM" -> "Jul 2021"
 */
private fun formatDateForYear(date: String): String {
    val formatter = SimpleDateFormat("MMM d, yyyy h:mm a", Locale.US)
    val parsedDate = formatter.parse(date)
    val outputFormatter = SimpleDateFormat("MMM yyyy", Locale.US)
    return parsedDate?.let { outputFormatter.format(it) } ?: date
}

/**
 * Helper function to format the volume in a more readable format
 * Ex. 12345678 -> "12.35M"
 */
private fun formatVolume(volume: Long): String {
    return when {
        volume >= 1_000_000_000_000 -> String.format(
            locale = Locale.US,
            "%.2fT",
            volume / 1_000_000_000_000.0
        )

        volume >= 1_000_000_000 -> String.format(
            locale = Locale.US,
            "%.2fB",
            volume / 1_000_000_000.0
        )

        volume >= 1_000_000 -> String.format(locale = Locale.US, "%.2fM", volume / 1_000_000.0)
        volume >= 1_000 -> String.format(locale = Locale.US, "%.2fK", volume / 1_000.0)
        else -> volume.toString()
    }
}