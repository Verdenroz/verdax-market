package com.verdenroz.verdaxmarket.feature.watchlist

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.verdenroz.verdaxmarket.core.designsystem.components.VxmBackIconButton
import com.verdenroz.verdaxmarket.core.designsystem.components.VxmTopAppBar
import com.verdenroz.verdaxmarket.core.designsystem.theme.ThemePreviews
import com.verdenroz.verdaxmarket.core.model.WatchlistQuote
import com.verdenroz.verdaxmarket.feature.watchlist.components.EditableWatchlistQuote
import sh.calvin.reorderable.ReorderableColumn

@Composable
internal fun EditWatchlistRoute(
    onNavigateBack: () -> Unit,
    watchlistViewModel: WatchlistViewModel = hiltViewModel()
) {
    val watchlist by watchlistViewModel.watchlist.collectAsStateWithLifecycle()
    val editableWatchlist = remember {
        mutableStateListOf<WatchlistQuote>()
    }

    LaunchedEffect(watchlist) {
        if (editableWatchlist.isEmpty() && watchlist.isNotEmpty()) {
            editableWatchlist.addAll(watchlist)
        }
    }
    EditWatchlistScreen(
        watchlist = editableWatchlist.toList(),
        onNavigateBack = onNavigateBack,
        onSave = {
            val updatedList = editableWatchlist.mapIndexed { index, quote ->
                quote.copy(order = index)
            }
            watchlistViewModel.updateWatchlist(updatedList)
            onNavigateBack()
        },
        onDelete = { quote ->
            editableWatchlist.remove(quote)
            // Update order values after deletion
            editableWatchlist.forEachIndexed { index, item ->
                editableWatchlist[index] = item.copy(order = index)
            }
        },
        onSettle = { fromIndex, toIndex ->
            if (fromIndex != toIndex) {
                val item = editableWatchlist.removeAt(fromIndex)
                editableWatchlist.add(toIndex, item)
                // Update order values after reordering
                editableWatchlist.forEachIndexed { index, quote ->
                    editableWatchlist[index] = quote.copy(order = index)
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditWatchlistScreen(
    watchlist: List<WatchlistQuote>,
    onNavigateBack: () -> Unit,
    onSave: () -> Unit = {},
    onDelete: (WatchlistQuote) -> Unit = {},
    onSettle: (fromIndex: Int, toIndex: Int) -> Unit = { _, _ -> }
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
        val haptic = LocalHapticFeedback.current
        if (watchlist.isNotEmpty()) {
            ReorderableColumn(
                list = watchlist,
                onSettle = onSettle,
                onMove = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                },
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) { index, quote, isDragging ->
                // Use both symbol and order as key to ensure unique identification
                key("${quote.symbol}_${quote.order}") {
                    val interactionSource = remember { MutableInteractionSource() }
                    EditableWatchlistQuote(
                        quote = quote,
                        dragModifier = Modifier
                            .draggableHandle(
                                onDragStarted = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                },
                                onDragStopped = { },
                                interactionSource = interactionSource,
                            )
                            .clearAndSetSemantics { },
                        onDelete = onDelete,
                    )
                }
            }
        }
    }
}

@ThemePreviews
@Composable
private fun PreviewEditWatchlistScreen() {
    EditWatchlistScreen(
        watchlist = remember {
            mutableStateListOf(
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
            )
        },
        onSave = { },
        onNavigateBack = { },
        onDelete = { }
    )
}

