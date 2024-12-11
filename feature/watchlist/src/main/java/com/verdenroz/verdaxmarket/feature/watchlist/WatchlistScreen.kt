package com.verdenroz.verdaxmarket.feature.watchlist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.verdenroz.verdaxmarket.core.designsystem.icons.VxmIcons
import com.verdenroz.verdaxmarket.core.designsystem.theme.ThemePreviews
import com.verdenroz.verdaxmarket.core.designsystem.theme.VxmTheme
import com.verdenroz.verdaxmarket.core.designsystem.util.UiText
import com.verdenroz.verdaxmarket.core.designsystem.util.asUiText
import com.verdenroz.verdaxmarket.core.model.WatchlistQuote
import com.verdenroz.verdaxmarket.feature.watchlist.components.ClearWatchlistFab
import com.verdenroz.verdaxmarket.feature.watchlist.components.QuoteSneakPeek
import com.verdenroz.verdaxmarket.feature.watchlist.components.WatchlistDialog
import com.verdenroz.verdaxmarket.feature.watchlist.components.WatchlistedQuote
import kotlinx.coroutines.launch

@Composable
internal fun WatchlistRoute(
    onNavigateToQuote: (String) -> Unit,
    onShowSnackbar: suspend (String, String?, SnackbarDuration) -> Boolean,
    watchlistViewModel: WatchlistViewModel = hiltViewModel()
) {
    val symbols by watchlistViewModel.watchlist.collectAsStateWithLifecycle()
    val watchlistState by watchlistViewModel.watchlistState.collectAsStateWithLifecycle()
    WatchlistScreen(
        watchlist = symbols,
        watchlistState = watchlistState,
        onNavigateToQuote = onNavigateToQuote,
        onShowSnackbar = onShowSnackbar,
        deleteFromWatchlist = watchlistViewModel::deleteFromWatchlist,
        clearWatchlist = watchlistViewModel::clearWatchlist
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun WatchlistScreen(
    watchlist: List<WatchlistQuote>,
    watchlistState: WatchlistState,
    onNavigateToQuote: (String) -> Unit,
    onShowSnackbar: suspend (String, String?, SnackbarDuration) -> Boolean,
    deleteFromWatchlist: (String) -> Unit,
    clearWatchlist: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState()
    var quote by remember { mutableStateOf(watchlist.firstOrNull()) }
    var isBottomSheetExpanded by remember { mutableStateOf(false) }
    var isManagingWatchlist by remember { mutableStateOf(false) }

    LaunchedEffect(bottomSheetScaffoldState.bottomSheetState) {
        snapshotFlow { bottomSheetScaffoldState.bottomSheetState.targetValue.ordinal }
            .collect { state ->
                isBottomSheetExpanded = (state != 2)
            }
    }

    BottomSheetScaffold(
        sheetContent = {
            if (watchlistState is WatchlistState.Success && quote != null) {
                QuoteSneakPeek(
                    quote = quote!!.copy(
                        change = quote!!.change ?: "",
                        percentChange = quote!!.percentChange ?: ""
                    ),
                    deleteFromWatchlist = { symbol ->
                        deleteFromWatchlist(symbol)
                        if (quote!!.symbol == symbol) {
                            val nextQuote = watchlistState.data.values.firstOrNull { quote -> quote.symbol != symbol }
                            if (nextQuote != null) {
                                quote = nextQuote
                            }
                        }
                    },
                    modifier = Modifier.clickable { onNavigateToQuote(quote!!.symbol) }
                )
            }
        },
        scaffoldState = bottomSheetScaffoldState
    ) { bottomSheetPadding ->
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(id = R.string.feature_watchlist_title)) },
                    actions = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .clickable(onClick = { isManagingWatchlist = !isManagingWatchlist })
                        ) {
                            Text(
                                text = stringResource(id = R.string.feature_watchlist_manage_watchlist),
                            )
                            Icon(
                                imageVector = VxmIcons.Settings,
                                contentDescription = stringResource(id = R.string.feature_watchlist_manage_watchlist),
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                )
            },
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottomSheetPadding)
        ) { padding ->
            when (watchlistState) {
                is WatchlistState.Loading -> {
                    WatchlistSkeleton(
                        watchlist = watchlist,
                        onNavigateToQuote = onNavigateToQuote,
                        deleteFromWatchlist = deleteFromWatchlist,
                        clearWatchlist = clearWatchlist
                    )
                }

                is WatchlistState.Error -> {
                    WatchlistSkeleton(
                        watchlist = watchlist,
                        onNavigateToQuote = onNavigateToQuote,
                        deleteFromWatchlist = deleteFromWatchlist,
                        clearWatchlist = clearWatchlist
                    )

                    LaunchedEffect(watchlistState.error) {
                        onShowSnackbar(
                            watchlistState.error.asUiText().asString(context),
                            UiText.StringResource(R.string.feature_watchlist_dismiss)
                                .asString(context),
                            SnackbarDuration.Short
                        )
                    }
                }

                is WatchlistState.Success -> {
                    if (watchlistState.data.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(id = R.string.feature_watchlist_empty),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            )
                        }
                        return@Scaffold
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .padding(bottom = if (isBottomSheetExpanded) 64.dp else 0.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(
                            items = watchlist,
                            key = { it.symbol }
                        ) { watchlistQuote ->
                            val loadedQuote = watchlistState.data[watchlistQuote.symbol]
                            // Prefer the loaded quote if available since it has the latest data
                            WatchlistedQuote(
                                quote = loadedQuote ?: watchlistQuote,
                                onNavigateToQuote = onNavigateToQuote,
                                deleteFromWatchList = {
                                    deleteFromWatchlist(it)
                                    if (quote?.symbol == it) {
                                        val nextQuote =
                                            watchlistState.data.values.firstOrNull { quote -> quote.symbol != it }
                                        if (nextQuote != null) {
                                            quote = nextQuote
                                        }
                                    }
                                },
                                onClick = {
                                    quote = loadedQuote ?: watchlistQuote
                                    scope.launch {
                                        bottomSheetScaffoldState.bottomSheetState.expand()
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun WatchlistSkeleton(
    watchlist: List<WatchlistQuote>,
    onNavigateToQuote: (String) -> Unit,
    deleteFromWatchlist: (String) -> Unit,
    clearWatchlist: () -> Unit
) {
    Scaffold(
        floatingActionButton = {
            ClearWatchlistFab(
                fabPadding = 64.dp,
                clearWatchlist = clearWatchlist,
            )
        },
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(
                items = watchlist,
                key = { it.symbol }
            ) { watchlistQuote ->
                WatchlistedQuote(
                    quote = watchlistQuote,
                    onNavigateToQuote = onNavigateToQuote,
                    deleteFromWatchList = deleteFromWatchlist,
                    onClick = { }
                )
            }
        }
    }
}

@ThemePreviews
@Composable
private fun PreviewWatchlistScreen() {
    VxmTheme {
        WatchlistScreen(
            watchlist = listOf(
                WatchlistQuote(
                    symbol = "AAPL",
                    name = "Apple Inc.",
                    price = "145.86",
                    change = "-0.23",
                    percentChange = "-0.16",
                    logo = null,
                    order = 0
                ),
                WatchlistQuote(
                    symbol = "TSLA",
                    name = "Tesla Inc. dddddddddddddddddddddddddddddddddd",
                    price = "678.90",
                    change = "+0.23",
                    percentChange = "+0.03",
                    logo = null,
                    order = 1
                ),
            ),
            watchlistState = WatchlistState.Success(
                data = mapOf(
                    "AAPL" to WatchlistQuote(
                        symbol = "AAPL",
                        name = "Apple Inc.",
                        price = "145.86",
                        change = "-0.23",
                        percentChange = "-0.16%",
                        logo = null,
                        order = 0
                    ),
                    "TSLA" to WatchlistQuote(
                        symbol = "TSLA",
                        name = "Tesla Inc.",
                        price = "1678.90",
                        change = "+0.23",
                        percentChange = "+0.03%",
                        logo = null,
                        order = 1
                    ),
                )
            ),
            onNavigateToQuote = {},
            onShowSnackbar = { _, _, _ -> true },
            deleteFromWatchlist = {},
            clearWatchlist = {}
        )
    }
}

@ThemePreviews
@Composable
private fun PreviewLoading() {
    VxmTheme {
        Surface {
            WatchlistSkeleton(
                watchlist = listOf(
                    WatchlistQuote(
                        symbol = "AAPL",
                        name = "Apple Inc.",
                        price = "145.86",
                        change = "-0.23",
                        percentChange = "-0.16%",
                        logo = null,
                        order = 0
                    ),
                    WatchlistQuote(
                        symbol = "TSLA",
                        name = "Tesla Inc.",
                        price = "678.90",
                        change = "+0.23",
                        percentChange = "+0.03%",
                        logo = null,
                        order = 1
                    ),
                ),
                onNavigateToQuote = {},
                deleteFromWatchlist = {},
                clearWatchlist = {}
            )
        }
    }
}