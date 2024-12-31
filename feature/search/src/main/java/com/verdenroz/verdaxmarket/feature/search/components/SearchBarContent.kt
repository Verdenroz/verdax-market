package com.verdenroz.verdaxmarket.feature.search.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.verdenroz.verdaxmarket.core.designsystem.icons.VxmIcons
import com.verdenroz.verdaxmarket.core.designsystem.theme.ThemePreviews
import com.verdenroz.verdaxmarket.core.designsystem.theme.VxmTheme
import com.verdenroz.verdaxmarket.core.model.SimpleQuoteData
import com.verdenroz.verdaxmarket.core.network.model.SearchResult
import com.verdenroz.verdaxmarket.feature.search.R
import com.verdenroz.verdaxmarket.feature.search.SearchState

@Composable
internal fun SearchBarContent(
    searchState: SearchState,
    searchResults: List<SearchResult>,
    recentQueries: List<String>,
    recentSymbolNames: List<Triple<String, String, String?>>,
    resultsInWatchlist: Map<String, Boolean>,
    recentQuotesInWatchlist: Map<String, Boolean>,
    onSearch: () -> Unit,
    onClick: (SearchResult) -> Unit,
    onNavigateToQuote: (String) -> Unit,
    addToWatchlist: (String, String, String?) -> Unit,
    deleteFromWatchlist: (String) -> Unit,
    onRecentQueryClick: (String) -> Unit,
    removeRecentQuery: (String) -> Unit,
    removeRecentQuote: (String) -> Unit,
    clearRecentQueries: () -> Unit,
    clearRecentQuotes: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        searchResults.forEachIndexed { index, match ->
            VxmListItem(
                modifier = Modifier
                    .clickable {
                        onSearch()
                        onClick(match)
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
                    if (resultsInWatchlist[match.symbol] == true) {
                        IconButton(onClick = { deleteFromWatchlist(match.symbol) }) {
                            Icon(
                                VxmIcons.Remove,
                                contentDescription = stringResource(id = R.string.feature_search_remove)
                            )
                        }
                    } else {
                        IconButton(onClick = { addToWatchlist(match.symbol, match.name, null) }) {
                            Icon(
                                VxmIcons.Add,
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
            onClick = onRecentQueryClick,
            clearAll = clearRecentQueries
        )

        when (searchState) {
            is SearchState.Loading -> {
                RecentQuotesSkeleton(recentSymbolNames)
            }

            is SearchState.Error -> {
                // Show error message
            }

            is SearchState.Success -> {
                RecentQuotes(
                    recentQuotes = searchState.recentQuotes,
                    recentQuotesInWatchlist = recentQuotesInWatchlist,
                    removeQuote = removeRecentQuote,
                    onNavigateToQuote = onNavigateToQuote,
                    clearAll = clearRecentQuotes,
                    addToWatchlist = addToWatchlist,
                    deleteFromWatchlist = deleteFromWatchlist
                )
            }
        }
    }
}

@ThemePreviews
@Composable
private fun PreviewSearchBarContent() {
    val matches = listOf(
        SearchResult(
            symbol = "AAPL",
            name = "Apple Inc.",
            exchangeShortName = "NASDAQ",
            exchange = "NASDAQ",
            type = "stock",
            objectID = "1"
        ),
        SearchResult(
            symbol = "GOOGL",
            name = "Alphabet Inc.",
            exchangeShortName = "NASDAQ",
            exchange = "NASDAQ",
            type = "stock",
            objectID = "2"
        ),
        SearchResult(
            symbol = "MSFT",
            name = "Microsoft Corp.",
            exchangeShortName = "NASDAQ",
            exchange = "NASDAQ",
            type = "stock",
            objectID = "3"
        )
    )

    val recentQueries = listOf(
        "AAPL",
        "GOOGL",
        "MSFT"
    )

    val recentQuotes = listOf(
        SimpleQuoteData(
            symbol = "AAPL",
            name = "Apple Inc.",
            price = "145.86",
            change = "+0.01",
            percentChange = "+0.01%",
            logo = "https://logo.clearbit.com/apple.com",
        ),
        SimpleQuoteData(
            symbol = "GOOGL",
            name = "Alphabet Inc.",
            price = "2734.87",
            change = "+0.02",
            percentChange = "+0.02%",
            logo = "https://logo.clearbit.com/abc.xyz",
        ),
        SimpleQuoteData(
            symbol = "MSFT",
            name = "Microsoft Corp.",
            price = "299.35",
            change = "+0.03",
            percentChange = "+0.03%",
            logo = "https://logo.clearbit.com/microsoft.com",
        )
    )

    VxmTheme {
        Column {
            SearchBarContent(
                searchState = SearchState.Success(recentQuotes),
                searchResults = matches,
                recentQueries = recentQueries,
                recentSymbolNames = listOf(
                    Triple("AAPL", "Apple Inc.", "https://logo.clearbit.com/apple.com"),
                    Triple("GOOGL", "Alphabet Inc.", "https://logo.clearbit.com/abc.xyz"),
                    Triple("MSFT", "Microsoft Corp.", "https://logo.clearbit.com/microsoft.com")
                ),
                resultsInWatchlist = mapOf(
                    "AAPL" to true,
                    "GOOGL" to false,
                    "MSFT" to false
                ),
                recentQuotesInWatchlist = mapOf(
                    "AAPL" to true,
                    "GOOGL" to false,
                    "MSFT" to false
                ),
                onSearch = {},
                onClick = {},
                onNavigateToQuote = {},
                addToWatchlist = { _, _, _ -> },
                deleteFromWatchlist = {},
                onRecentQueryClick = {},
                removeRecentQuery = {},
                removeRecentQuote = {},
                clearRecentQueries = {},
                clearRecentQuotes = {}
            )
        }
    }
}