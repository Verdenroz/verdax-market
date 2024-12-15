package com.verdenroz.verdaxmarket.feature.watchlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.verdenroz.verdaxmarket.core.designsystem.components.VxmBackIconButton
import com.verdenroz.verdaxmarket.core.designsystem.components.VxmTopAppBar
import com.verdenroz.verdaxmarket.core.designsystem.theme.ThemePreviews
import com.verdenroz.verdaxmarket.core.model.WatchlistQuote
import com.verdenroz.verdaxmarket.feature.watchlist.components.EditableWatchlistQuote

@Composable
internal fun EditWatchlistRoute(
    onNavigateBack: () -> Unit,
    watchlistViewModel: WatchlistViewModel = hiltViewModel()
) {
    val watchlist by watchlistViewModel.watchlist.collectAsStateWithLifecycle()
    var editableWatchlist by remember { mutableStateOf<List<WatchlistQuote>>(emptyList()) }

    LaunchedEffect(watchlist) {
        if (watchlist.isNotEmpty()) {
            editableWatchlist = watchlist
        }
    }

    EditWatchlistScreen(
        watchlist = editableWatchlist,
        onNavigateBack = onNavigateBack,
        onSave = {
            watchlistViewModel.updateWatchlist(editableWatchlist)
            onNavigateBack()
        },
        onDelete = { symbol ->
            editableWatchlist = editableWatchlist.filterNot { it.symbol == symbol }
        },
        onMove = { fromIndex, toIndex ->
            editableWatchlist = editableWatchlist.toMutableList().apply {
                val item = removeAt(fromIndex)
                add(toIndex, item)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditWatchlistScreen(
    watchlist: List<WatchlistQuote> = emptyList(),
    onNavigateBack: () -> Unit,
    onSave: () -> Unit = {},
    onDelete: (String) -> Unit = {},
    onMove: (fromIndex: Int, toIndex: Int) -> Unit = { _, _ -> }
) {
    Scaffold(
        topBar = {
            VxmTopAppBar(
                title = {
                    Text(text = "Edit Watchlist")
                },
                navigationIcon = {
                    VxmBackIconButton(onClick = onNavigateBack)
                },
                actions = {
                    Button(
                        onClick = onSave
                    ) {
                        Text(text = "Save")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            contentPadding = padding,
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        ) {
            items(watchlist, key = { it.symbol }) { quote ->
                EditableWatchlistQuote(
                    quote = quote,
                    watchlistSize = watchlist.size,
                    onDelete = onDelete,
                    onMove = onMove
                )
            }
        }
    }
}

@ThemePreviews
@Composable
private fun PreviewEditWatchlistScreen() {
    EditWatchlistScreen(
        watchlist = listOf(
            WatchlistQuote(
                symbol = "AAPL",
                name = "Apple Inc.",
                price = "123.45",
                change = "+1.23",
                percentChange = "+1.00%",
                logo = null,
                order = 0
            ),
            WatchlistQuote(
                symbol = "GOOGL",
                name = "Alphabet Inc.",
                price = "234.56",
                change = "-2.34",
                percentChange = "-2.00%",
                logo = null,
                order = 1
            ),
        ),
        onSave = { },
        onNavigateBack = { },
        onDelete = { }
    )
}


