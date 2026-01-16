package com.verdenroz.verdaxmarket.core.data.model

import com.verdenroz.verdaxmarket.core.model.News
import com.verdenroz.verdaxmarket.core.network.model.NewsWrapperDto
import com.verdenroz.verdaxmarket.core.network.model.NewsArticleDto
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

/**
 * Helper function to format news timestamp to relative time or absolute time.
 * Recent news shows relative time ("5m ago", "2h ago"), older news shows date.
 * @param timestamp Unix timestamp in seconds
 * @return Formatted time string
 */
private fun formatNewsTime(timestamp: Long): String {
    return try {
        val newsInstant = Instant.ofEpochSecond(timestamp)
        val now = Instant.now()
        val minutesAgo = ChronoUnit.MINUTES.between(newsInstant, now)

        when {
            minutesAgo < 1 -> "Just now"
            minutesAgo < 60 -> "${minutesAgo}m ago"
            minutesAgo < 1440 -> "${minutesAgo / 60}h ago"
            minutesAgo < 10080 -> "${minutesAgo / 1440}d ago"
            else -> {
                // For older news, show the date
                val formatter = DateTimeFormatter.ofPattern("MMM d, yyyy")
                    .withZone(ZoneId.systemDefault())
                formatter.format(newsInstant)
            }
        }
    } catch (_: Exception) {
        timestamp.toString()
    }
}

/**
 * Maps v2 NewsArticleDto to News domain model.
 * Handles flexible field names from different API versions.
 */
fun NewsArticleDto.asExternalModel() = News(
    title = title,
    link = link,
    source = publisher ?: source ?: "",
    img = img ?: thumbnail?.resolutions?.firstOrNull()?.url ?: "",
    time = run {
        val timestamp = providerPublishTime
        when {
            timestamp != null -> formatNewsTime(timestamp)
            time != null -> time ?: "Unknown"
            else -> "Unknown"
        }
    },
)

/**
 * Maps v2 NewsWrapperDto to a list of News domain models.
 */
fun NewsWrapperDto.asExternalModel(): List<News> {
    return news.map { it.asExternalModel() }
}

/**
 * Maps a list of NewsArticleDto to a list of News domain models.
 */
fun List<NewsArticleDto>.asExternalModel(): List<News> {
    return map { it.asExternalModel() }
}