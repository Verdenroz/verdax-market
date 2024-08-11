package com.verdenroz.verdaxmarket.feature.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.verdenroz.verdaxmarket.core.designsystem.components.VxmCheckbox
import com.verdenroz.verdaxmarket.core.designsystem.components.VxmFilterChip
import com.verdenroz.verdaxmarket.core.designsystem.components.VxmListItem
import com.verdenroz.verdaxmarket.core.designsystem.components.VxmSearchBar
import com.verdenroz.verdaxmarket.core.designsystem.theme.ThemePreviews
import com.verdenroz.verdaxmarket.core.model.RegionFilter
import com.verdenroz.verdaxmarket.core.model.TypeFilter
import com.verdenroz.verdaxmarket.core.network.model.SearchResult
import com.verdenroz.verdaxmarket.feature.quotes.navigation.navigateToQuote
import kotlinx.coroutines.delay

@Composable
fun SearchBar(
    navController: NavController,
    searchViewModel: SearchViewModel = hiltViewModel()
) {
    val typeFilter by searchViewModel.typeFilter.collectAsStateWithLifecycle()
    val regionFilter by searchViewModel.regionFilter.collectAsStateWithLifecycle()
    val searchResults by searchViewModel.searchResults.collectAsStateWithLifecycle()
    val resultsInWatchlist by searchViewModel.resultsInWatchlist.collectAsStateWithLifecycle()

    SearchBarContent(
        navController = navController,
        searchResults = searchResults,
        resultsInWatchlist = resultsInWatchlist,
        regionFilter = regionFilter,
        typeFilters = typeFilter,
        updateRegionFilter = searchViewModel::updateRegionFilter,
        updateTypeFilter = searchViewModel::updateTypeFilter,
        updateQuery = searchViewModel::updateQuery,
        search = searchViewModel::search,
        addToWatchList = searchViewModel::addToWatchlist,
        deleteFromWatchList = searchViewModel::deleteFromWatchlist
    )
}

@Composable
internal fun SearchBarContent(
    navController: NavController,
    searchResults: List<SearchResult>,
    resultsInWatchlist: List<Boolean>,
    regionFilter: RegionFilter,
    typeFilters: List<TypeFilter>,
    updateRegionFilter: (RegionFilter) -> Unit,
    updateTypeFilter: (TypeFilter) -> Unit,
    updateQuery: (String) -> Unit,
    search: (String) -> Unit,
    addToWatchList: (SearchResult) -> Unit,
    deleteFromWatchList: (SearchResult) -> Unit,
) {
    var query by rememberSaveable { mutableStateOf("") }
    var active by rememberSaveable { mutableStateOf(false) }
    var showFilters by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(query) {
        delay(250)
        search(query)
    }

    VxmSearchBar(
        query = query,
        active = active,
        onQueryChange = {
            query = it
            updateQuery(it)
        },
        onSearch = { active = false },
        onActiveChange = { active = it },
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
        searchResults.forEachIndexed { index, match ->
            VxmListItem(
                modifier = Modifier
                    .clickable {
                        active = false
                        navController.navigateToQuote(match.symbol)
                    }
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
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
        }
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
private fun PreviewSearchList() {
    val match = SearchResult(
        symbol = "AAPL",
        name = "Apple Inc.",
        exchangeShortName = "NASDAQ",
        exchange = "NASDAQ",
        type = "stock"
    )
    ListItem(
        headlineContent = { Text(match.name) },
        leadingContent = { Text(match.symbol) },
        trailingContent = { Icon(Icons.Default.AddCircle, contentDescription = null) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    )
}