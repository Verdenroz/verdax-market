package com.verdenroz.verdaxmarket.core.data.model

import com.verdenroz.verdaxmarket.core.model.HistoricalData
import com.verdenroz.verdaxmarket.core.network.model.ChartDto
import com.verdenroz.verdaxmarket.core.network.model.CandleDto
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Helper function to format a Unix timestamp to a date string based on interval.
 * @param timestamp Unix timestamp in seconds
 * @param interval The chart interval (e.g., "1m", "5m", "1h", "1d", "1wk", "1mo")
 * @return Formatted date string
 */
private fun formatTimestamp(timestamp: Long, interval: String?): String {
    return try {
        val instant = Instant.ofEpochSecond(timestamp)
        val formatter = when {
            interval == null -> DateTimeFormatter.ofPattern("MMM d, yyyy")
            interval.contains("m") || interval.contains("h") -> {
                // Intraday intervals: show date and time
                DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a")
            }
            else -> {
                // Daily or longer intervals: show only date
                DateTimeFormatter.ofPattern("MMM d, yyyy")
            }
        }
        formatter.withZone(ZoneId.systemDefault()).format(instant)
    } catch (_: Exception) {
        timestamp.toString()
    }
}

/**
 * Maps v2 CandleDto to HistoricalData domain model.
 * @param interval The chart interval for proper timestamp formatting
 */
fun CandleDto.asExternalModel(@Suppress("UNUSED_PARAMETER") interval: String? = null) = HistoricalData(
    open = open?.toFloat() ?: 0f,
    high = high?.toFloat() ?: 0f,
    low = low?.toFloat() ?: 0f,
    close = close?.toFloat() ?: 0f,
    volume = formatVolume(volume ?: 0L),
)

/**
 * Maps v2 ChartDto to a Map of date strings to HistoricalData.
 * This maintains compatibility with the existing domain model structure.
 */
fun ChartDto.asExternalModel(): Map<String, HistoricalData> {
    return candles.associate { candle ->
        val dateKey = formatTimestamp(candle.timestamp, interval)
        dateKey to candle.asExternalModel(interval)
    }
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
