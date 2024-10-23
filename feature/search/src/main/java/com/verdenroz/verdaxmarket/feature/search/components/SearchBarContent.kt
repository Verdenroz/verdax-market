package com.verdenroz.verdaxmarket.feature.search.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.verdenroz.verdaxmarket.core.designsystem.components.VxmListItem
import com.verdenroz.verdaxmarket.core.designsystem.theme.ThemePreviews
import com.verdenroz.verdaxmarket.core.designsystem.theme.VxmTheme
import com.verdenroz.verdaxmarket.core.model.RecentQuoteResult
import com.verdenroz.verdaxmarket.core.model.RecentSearchQuery
import com.verdenroz.verdaxmarket.core.network.model.SearchResult
import com.verdenroz.verdaxmarket.feature.search.R
import kotlinx.datetime.Clock

@Composable
internal fun SearchBarContent(
    searchResults: List<SearchResult>,
    resultsInWatchlist: List<Boolean>,
    recentQueries: List<RecentSearchQuery>,
    recentQuotes: List<RecentQuoteResult>,
    onClick: (String) -> Unit,
    onNavigateToQuote: (String) -> Unit,
    addToWatchList: (SearchResult) -> Unit,
    deleteFromWatchList: (SearchResult) -> Unit,
    onRecentQueryClick: (String) -> Unit,
    removeRecentQuery: (RecentSearchQuery) -> Unit,
    removeRecentQuote: (RecentQuoteResult) -> Unit,
    clearRecentQueries: () -> Unit,
    clearRecentQuotes: () -> Unit
) {
    searchResults.forEachIndexed { index, match ->
        VxmListItem(
            modifier = Modifier
                .clickable {
                    onClick(match.symbol)
                    onNavigateToQuote(match.symbol)
                }
                .fillMaxWidth(),
            headlineContent = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = match.symbol,
                        style = MaterialTheme.typography.titleMedium,
                    )

                    Text(
                        text = match.exchangeShortName,
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            },
            supportingContent = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = match.name,
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 16.dp)
                    )
                    Text(
                        text = match.type,
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            },
            trailingContent = {
                if (resultsInWatchlist.getOrNull(index) == true) {
                    IconButton(onClick = { deleteFromWatchList(match) }) {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = stringResource(id = R.string.feature_search_remove)
                        )
                    }
                } else {
                    IconButton(onClick = { addToWatchList(match) }) {
                        Icon(
                            Icons.Default.AddCircle,
                            contentDescription = stringResource(id = R.string.feature_search_add)
                        )
                    }
                }
            },
        )
        HorizontalDivider()
    }

    RecentQueries(
        recentQueries = recentQueries,
        removeRecentQuery = removeRecentQuery,
        onClick =  onRecentQueryClick,
        clearAll = clearRecentQueries
    )

    RecentQuotes(
        recentQuotes = recentQuotes,
        removeQuote = removeRecentQuote,
        onNavigateToQuote = onNavigateToQuote,
        clearAll = clearRecentQuotes,
    )

}

@ThemePreviews
@Composable
private fun PreviewSearchBarContent() {
    val match = SearchResult(
        symbol = "AAPL",
        name = "Apple Inc.",
        exchangeShortName = "NASDAQ",
        exchange = "NASDAQ",
        type = "stock"
    )

    VxmTheme {
        Column {
            SearchBarContent(
                searchResults = List(3) { match },
                resultsInWatchlist = List(3) { false },
                recentQueries = List(3) { RecentSearchQuery("AAPL") },
                recentQuotes = List(3) { RecentQuoteResult(
                    symbol = "AAPL",
                    name = "Apple Inc.",
                    price = 145.86,
                    change = "+0.01",
                    percentChange = "+0.01%",
                    logo = "https://logo.clearbit.com/apple.com",
                    timestamp = Clock.System.now()
                ) },
                onClick = {},
                onNavigateToQuote = {},
                addToWatchList = {},
                deleteFromWatchList = {},
                onRecentQueryClick = {},
                removeRecentQuery = {},
                removeRecentQuote = {},
                clearRecentQueries = {},
                clearRecentQuotes = {}
            )
        }
    }
}