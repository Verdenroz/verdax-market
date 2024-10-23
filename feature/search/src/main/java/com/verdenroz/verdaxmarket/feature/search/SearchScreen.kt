package com.verdenroz.verdaxmarket.feature.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.verdenroz.verdaxmarket.core.designsystem.components.VxmCheckbox
import com.verdenroz.verdaxmarket.core.designsystem.components.VxmFilterChip
import com.verdenroz.verdaxmarket.core.designsystem.components.VxmSearchBar
import com.verdenroz.verdaxmarket.core.designsystem.theme.ThemePreviews
import com.verdenroz.verdaxmarket.core.designsystem.theme.VxmTheme
import com.verdenroz.verdaxmarket.core.model.RecentQuoteResult
import com.verdenroz.verdaxmarket.core.model.RecentSearchQuery
import com.verdenroz.verdaxmarket.core.model.RegionFilter
import com.verdenroz.verdaxmarket.core.model.TypeFilter
import com.verdenroz.verdaxmarket.core.network.model.SearchResult
import com.verdenroz.verdaxmarket.feature.search.components.SearchBarContent
import kotlinx.coroutines.delay

@Composable
internal fun SearchRoute(
    onNavigateToQuote: (String) -> Unit,
    searchViewModel: SearchViewModel = hiltViewModel()
) {
    val typeFilter by searchViewModel.typeFilter.collectAsStateWithLifecycle()
    val regionFilter by searchViewModel.regionFilter.collectAsStateWithLifecycle()
    val searchResults by searchViewModel.searchResults.collectAsStateWithLifecycle()
    val resultsInWatchlist by searchViewModel.resultsInWatchlist.collectAsStateWithLifecycle()
    val recentQueries by searchViewModel.recentQueries.collectAsStateWithLifecycle()
    val recentQuotes by searchViewModel.recentQuotes.collectAsStateWithLifecycle()

    SearchScreen(
        onNavigateToQuote = onNavigateToQuote,
        searchResults = searchResults,
        resultsInWatchlist = resultsInWatchlist,
        recentQueries = recentQueries,
        recentQuotes = recentQuotes,
        regionFilter = regionFilter,
        typeFilters = typeFilter,
        updateRegionFilter = searchViewModel::updateRegionFilter,
        updateTypeFilter = searchViewModel::updateTypeFilter,
        updateQuery = searchViewModel::updateQuery,
        search = searchViewModel::search,
        onSearch = searchViewModel::onSearch,
        clearSearchResults = searchViewModel::clearSearchResults,
        restoreSearchResults = searchViewModel::restoreSearchResults,
        addToWatchList = searchViewModel::addToWatchlist,
        deleteFromWatchList = searchViewModel::deleteFromWatchlist,
        removeRecentQuery = searchViewModel::removeRecentQuery,
        removeRecentQuote = searchViewModel::removeRecentQuote,
        clearRecentQueries = searchViewModel::clearRecentQueries,
        clearRecentQuotes = searchViewModel::clearRecentQuotes

    )
}

@Composable
internal fun SearchScreen(
    searchResults: List<SearchResult>,
    resultsInWatchlist: List<Boolean>,
    recentQueries: List<RecentSearchQuery>,
    recentQuotes: List<RecentQuoteResult>,
    regionFilter: RegionFilter,
    typeFilters: List<TypeFilter>,
    onNavigateToQuote: (String) -> Unit,
    updateRegionFilter: (RegionFilter) -> Unit,
    updateTypeFilter: (TypeFilter) -> Unit,
    updateQuery: (String) -> Unit,
    search: (String) -> Unit,
    onSearch: (String) -> Unit,
    clearSearchResults: () -> Unit,
    restoreSearchResults: () -> Unit,
    addToWatchList: (SearchResult) -> Unit,
    deleteFromWatchList: (SearchResult) -> Unit,
    removeRecentQuery: (RecentSearchQuery) -> Unit,
    removeRecentQuote: (RecentQuoteResult) -> Unit,
    clearRecentQueries: () -> Unit,
    clearRecentQuotes: () -> Unit
) {
    var query by rememberSaveable { mutableStateOf("") }
    var expand: Boolean? by rememberSaveable { mutableStateOf(null) }
    var showFilters by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(query) {
        delay(250)
        search(query)
    }

    VxmSearchBar(
        query = query,
        onQueryChange = {
            query = it
            updateQuery(it)
            expand = if (it.isNotBlank()) true else null
        },
        expand = expand,
        onExpandChange = {
            expand = it
            when (it) {
                true -> restoreSearchResults()
                false -> clearSearchResults()
            }
        },
        onSearch = { queryString ->
            if (queryString.isNotBlank() && searchResults.isNotEmpty()) {
                onSearch(queryString)
            }
        },
        trailingIcon = {
            IconButton(onClick = { showFilters = !showFilters }) {
                Icon(
                    Icons.Default.MoreVert,
                    contentDescription = stringResource(id = R.string.feature_search_filter)
                )
            }
            DropdownMenu(
                expanded = showFilters,
                onDismissRequest = { showFilters = false },
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(.75f)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween,
                ) {
                    TypeCheckBoxContainer(
                        typeFilters = typeFilters,
                        updateTypeFilter = updateTypeFilter,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth()
                    )
                    RegionFilterContainer(
                        currentRegionFilter = regionFilter,
                        updateRegionFilter = updateRegionFilter,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth()
                    )
                }
            }
        }
    ) {
        SearchBarContent(
            searchResults = searchResults,
            resultsInWatchlist = resultsInWatchlist,
            recentQueries = recentQueries,
            recentQuotes = recentQuotes,
            onClick = { onSearch(query) },
            onNavigateToQuote = onNavigateToQuote,
            addToWatchList = addToWatchList,
            deleteFromWatchList = deleteFromWatchList,
            onRecentQueryClick = { recentQuery ->
                query = recentQuery
                updateQuery(recentQuery)
                search(recentQuery)
            },
            removeRecentQuery = removeRecentQuery,
            removeRecentQuote = removeRecentQuote,
            clearRecentQueries = clearRecentQueries,
            clearRecentQuotes = clearRecentQuotes
        )
    }
}

@Composable
private fun TypeCheckBoxContainer(
    typeFilters: List<TypeFilter>,
    updateTypeFilter: (TypeFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        TypeFilter.entries.forEach { type ->
            VxmCheckbox(
                checked = typeFilters.contains(type),
                onCheckedChange = { updateTypeFilter(type) },
                text = when (type) {
                    TypeFilter.STOCK -> stringResource(id = R.string.feature_search_stock)
                    TypeFilter.ETF -> stringResource(id = R.string.feature_search_etf)
                    TypeFilter.TRUST -> stringResource(id = R.string.feature_search_trust)
                }
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RegionFilterContainer(
    currentRegionFilter: RegionFilter,
    updateRegionFilter: (RegionFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        verticalArrangement = Arrangement.Center,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
    ) {
        RegionFilter.entries.forEach { region ->
            VxmFilterChip(
                selected = region == currentRegionFilter,
                onClick = { updateRegionFilter(region) },
                label = when (region) {
                    RegionFilter.US -> stringResource(id = R.string.feature_search_US)
                    RegionFilter.NA -> stringResource(id = R.string.feature_search_NA)
                    RegionFilter.SA -> stringResource(id = R.string.feature_search_SA)
                    RegionFilter.EU -> stringResource(id = R.string.feature_search_EU)
                    RegionFilter.AS -> stringResource(id = R.string.feature_search_AS)
                    RegionFilter.ME -> stringResource(id = R.string.feature_search_ME)
                    RegionFilter.AF -> stringResource(id = R.string.feature_search_AF)
                    RegionFilter.AU -> stringResource(id = R.string.feature_search_AU)
                    RegionFilter.GLOBAL -> stringResource(id = R.string.feature_search_GLOBAL)
                }
            )
        }
    }
}

@ThemePreviews
@Composable
private fun PreviewSearchScreen() {
    VxmTheme {
        SearchScreen(
            searchResults = emptyList(),
            resultsInWatchlist = emptyList(),
            recentQueries = emptyList(),
            recentQuotes = emptyList(),
            regionFilter = RegionFilter.US,
            typeFilters = emptyList(),
            onNavigateToQuote = {},
            updateRegionFilter = {},
            updateTypeFilter = {},
            updateQuery = {},
            search = {},
            onSearch = {},
            clearSearchResults = {},
            restoreSearchResults = {},
            addToWatchList = {},
            deleteFromWatchList = {},
            removeRecentQuery = {},
            removeRecentQuote = {},
            clearRecentQueries = {},
            clearRecentQuotes = {}
        )
    }
}