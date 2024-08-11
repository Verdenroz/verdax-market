package com.verdenroz.verdaxmarket.core.data.model

import com.verdenroz.verdaxmarket.core.model.HistoricalData
import com.verdenroz.verdaxmarket.core.network.model.HistoricalDataResponse
import java.util.Locale

fun HistoricalDataResponse.asExternalModel() = HistoricalData(
    open = open,
    high = high,
    low = low,
    close = close,
    volume = formatVolume(volume),
)

fun Map<String, HistoricalDataResponse>.asExternalModel(): Map<String, HistoricalData> =
    map { (date, data) -> date to data.asExternalModel() }.toMap()

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