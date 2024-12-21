package com.verdenroz.verdaxmarket.feature.watchlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.verdenroz.verdaxmarket.core.designsystem.components.VxmBackIconButton
import com.verdenroz.verdaxmarket.core.designsystem.components.VxmTopAppBar
import com.verdenroz.verdaxmarket.core.designsystem.theme.ThemePreviews
import com.verdenroz.verdaxmarket.core.designsystem.theme.VxmTheme
import com.verdenroz.verdaxmarket.core.model.WatchlistQuote
import com.verdenroz.verdaxmarket.feature.watchlist.components.EditableWatchlistQuote
import com.verdenroz.verdaxmarket.feature.watchlist.components.QuoteOptionsPeek
import com.verdenroz.verdaxmarket.feature.watchlist.components.QuoteSneakPeek
import kotlinx.coroutines.launch
import sh.calvin.reorderable.ReorderableColumn

@Composable
internal fun EditWatchlistRoute(
    onNavigateBack: () -> Unit,
    onNavigateToQuote: (String) -> Unit,
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
        onNavigateToQuote = onNavigateToQuote,
        onSave = {
            val updatedList = editableWatchlist.mapIndexed { index, quote ->
                quote.copy(order = index)
            }
            watchlistViewModel.updateWatchlist(updatedList)
            onNavigateBack()
        },
        onMoveUp = { quote ->
            val index = editableWatchlist.indexOf(editableWatchlist.find { it.symbol == quote.symbol })
            if (index > 0) {
                val item = editableWatchlist.removeAt(index)
                editableWatchlist.add(index - 1, item)
            }
        },
        onMoveDown = { quote ->
            val index = editableWatchlist.indexOf(editableWatchlist.find { it.symbol == quote.symbol })
            if (index < editableWatchlist.size - 1) {
                val item = editableWatchlist.removeAt(index)
                editableWatchlist.add(index + 1, item)
            }
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
    onNavigateToQuote: (String) -> Unit,
    onSave: () -> Unit,
    onMoveUp: (WatchlistQuote) -> Unit,
    onMoveDown: (WatchlistQuote) -> Unit,
    onDelete: (WatchlistQuote) -> Unit,
    onSettle: (fromIndex: Int, toIndex: Int) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(skipHiddenState = false)
    )
    var quote by rememberSaveable(stateSaver = WatchlistQuoteSaver) {
        mutableStateOf(null)
    }
    var bottomSheetMode by rememberSaveable(stateSaver = BottomSheetModeSaver) {
        mutableStateOf<BottomSheetMode>(BottomSheetMode.Preview)
    }

    BottomSheetScaffold(
        scaffoldState = bottomSheetScaffoldState,
        sheetContainerColor = MaterialTheme.colorScheme.surface,
        sheetContentColor = MaterialTheme.colorScheme.onSurface,
        sheetContent = {
            if (quote != null) {
                when (bottomSheetMode) {
                    BottomSheetMode.Preview -> {
                        QuoteSneakPeek(
                            quote = quote!!.copy(
                                change = quote!!.change ?: "",
                                percentChange = quote!!.percentChange ?: ""
                            ),
                            modifier = Modifier.clickable { onNavigateToQuote(quote!!.symbol) }
                        )
                    }
                    BottomSheetMode.Options ->
                        QuoteOptionsPeek(
                            quote = quote!!,
                            onNavigateToQuote = {
                                onNavigateToQuote(quote!!.symbol)
                            },
                            onMoveUp = {
                                scope.launch {
                                    onMoveUp(quote!!)
                                    bottomSheetScaffoldState.bottomSheetState.hide()
                                }
                            },
                            onMoveDown = {
                                scope.launch {
                                    onMoveDown(quote!!)
                                    bottomSheetScaffoldState.bottomSheetState.hide()
                                }
                            },
                            onDelete = {
                                scope.launch {
                                    onDelete(quote!!)
                                    bottomSheetScaffoldState.bottomSheetState.hide()
                                }
                            }
                        )
                }
            }
        },
        topBar = {
            VxmTopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.feature_watchlist_edit_watchlist_title))
                },
                navigationIcon = {
                    VxmBackIconButton(onClick = onNavigateBack)
                },
                actions = {
                    Button(
                        onClick = onSave
                    ) {
                        Text(text = stringResource(id = R.string.feature_watchlist_edit_watchlist_save))
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
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(padding)
            ) { index, watchlistQuote, isDragging ->
                key("${watchlistQuote.symbol}_${watchlistQuote.order}") {
                    val interactionSource = remember { MutableInteractionSource() }
                    EditableWatchlistQuote(
                        quote = watchlistQuote,
                        dragModifier = Modifier
                            .draggableHandle(
                                onDragStarted = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                },
                                onDragStopped = { },
                                interactionSource = interactionSource,
                            )
                            .clearAndSetSemantics { },
                        onClick = {
                            scope.launch {
                                quote = watchlistQuote
                                bottomSheetMode = BottomSheetMode.Preview
                                bottomSheetScaffoldState.bottomSheetState.expand()
                            }
                        },
                        onMoreClick = {
                            scope.launch {
                                quote = watchlistQuote
                                bottomSheetMode = BottomSheetMode.Options
                                bottomSheetScaffoldState.bottomSheetState.expand()
                            }
                        },
                        onNavigateToQuote = onNavigateToQuote,
                        onDelete = onDelete
                    )
                }
            }
        }
    }
}

@ThemePreviews
@Composable
private fun PreviewEditWatchlistScreen() {
    VxmTheme {
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
            onNavigateBack = { },
            onNavigateToQuote = { },
            onSave = { },
            onMoveUp = { },
            onMoveDown = { },
            onDelete = { },
            onSettle = { _, _ -> }
        )
    }
}

