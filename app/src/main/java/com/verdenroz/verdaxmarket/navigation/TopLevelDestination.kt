package com.verdenroz.verdaxmarket.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import com.verdenroz.verdaxmarket.core.designsystem.icons.VxmIcons
import com.verdenroz.verdaxmarket.feature.home.R as homeR
import com.verdenroz.verdaxmarket.feature.search.R as searchR
import com.verdenroz.verdaxmarket.feature.watchlist.R as watchlistR

enum class TopLevelDestination(
    val title: Int,
    val icon: ImageVector,
) {
    HOME(
        title = homeR.string.feature_home_title,
        icon = VxmIcons.Home
    ),
    SEARCH(
        title = searchR.string.feature_search_search,
        icon = VxmIcons.Search
    ),
    WATCHLIST(
        title = watchlistR.string.feature_watchlist_title,
        icon = VxmIcons.List
    )
}