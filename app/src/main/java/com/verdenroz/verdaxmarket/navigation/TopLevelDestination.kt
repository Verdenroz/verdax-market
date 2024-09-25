package com.verdenroz.verdaxmarket.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Search
import androidx.compose.ui.graphics.vector.ImageVector
import com.verdenroz.verdaxmarket.feature.home.R as homeR
import com.verdenroz.verdaxmarket.feature.search.R as searchR
import com.verdenroz.verdaxmarket.feature.watchlist.R as watchlistR

enum class TopLevelDestination(
    val title: Int,
    val icon: ImageVector,
) {
    HOME(
        title = homeR.string.feature_home_title,
        icon = Icons.Rounded.Home
    ),
    SEARCH(
        title = searchR.string.feature_search_search,
        icon = Icons.Rounded.Search
    ),
    WATCHLIST(
        title = watchlistR.string.feature_watchlist_title,
        icon = Icons.AutoMirrored.Rounded.List
    )
}