package com.verdenroz.verdaxmarket.core.network.model

import kotlinx.serialization.Serializable

/**
 * Wrapper DTO for news responses from the v2 API.
 * Used by /v2/news and /v2/news/{symbol}
 *
 * @param count The total number of news articles
 * @param news List of news articles
 */
@Serializable
data class NewsWrapperDto(
    val count: Int = 0,
    val news: List<NewsArticleDto> = emptyList(),
)

/**
 * DTO for a single news article from the v2 API.
 *
 * @param uuid Unique identifier for the news article (optional, may be called 'id')
 * @param id Alternative unique identifier
 * @param title The title of the news article
 * @param publisher The publisher/source of the news (optional, may be called 'source')
 * @param source Alternative source field
 * @param link URL to the full news article
 * @param providerPublishTime The timestamp when the article was published (Unix timestamp in seconds, optional)
 * @param time Alternative time field (may be string)
 * @param type The type of content (e.g., "STORY", "VIDEO")
 * @param thumbnail Thumbnail image information
 * @param img Direct image URL (alternative to thumbnail)
 * @param relatedTickers List of related stock symbols
 */
@Serializable
data class NewsArticleDto(
    val uuid: String? = null,
    val id: String? = null,
    val title: String,
    val publisher: String? = null,
    val source: String? = null,
    val link: String,
    val providerPublishTime: Long? = null,
    val time: String? = null,
    val type: String? = null,
    val thumbnail: NewsThumbnailDto? = null,
    val img: String? = null,
    val relatedTickers: List<String>? = null,
)

/**
 * DTO for news article thumbnail information.
 *
 * @param resolutions List of available thumbnail resolutions
 */
@Serializable
data class NewsThumbnailDto(
    val resolutions: List<ThumbnailResolutionDto> = emptyList(),
)

/**
 * DTO for a single thumbnail resolution.
 *
 * @param url The URL of the thumbnail image
 * @param width The width of the thumbnail in pixels
 * @param height The height of the thumbnail in pixels
 * @param tag Optional tag for the resolution (e.g., "original", "140x140")
 */
@Serializable
data class ThumbnailResolutionDto(
    val url: String,
    val width: Int? = null,
    val height: Int? = null,
    val tag: String? = null,
)
