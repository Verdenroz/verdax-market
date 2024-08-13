package com.verdenroz.verdaxmarket.core.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

/**
 * Represents a query that was recently searched.
 * @param query The query that was searched.
 * @param timeStamp The time when the query was searched.
 */
data class RecentSearchQuery(
    val query: String,
    val timeStamp: Instant = Clock.System.now(),
)
