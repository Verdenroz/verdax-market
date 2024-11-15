package com.verdenroz.verdaxmarket.feature.watchlist

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.verdenroz.verdaxmarket.core.common.error.DataError
import com.verdenroz.verdaxmarket.core.common.result.Result
import com.verdenroz.verdaxmarket.core.designsystem.icons.VxmIcons
import com.verdenroz.verdaxmarket.core.designsystem.theme.ThemePreviews
import com.verdenroz.verdaxmarket.core.designsystem.theme.VxmTheme
import com.verdenroz.verdaxmarket.core.designsystem.theme.getNegativeTextColor
import com.verdenroz.verdaxmarket.core.designsystem.theme.getPositiveTextColor
import com.verdenroz.verdaxmarket.core.designsystem.util.UiText
import com.verdenroz.verdaxmarket.core.designsystem.util.asUiText
import com.verdenroz.verdaxmarket.core.model.SimpleQuoteData
import com.verdenroz.verdaxmarket.feature.watchlist.components.ClearWatchlistFab
import com.verdenroz.verdaxmarket.feature.watchlist.components.QuoteSneakPeek
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.abs
import kotlin.math.pow

@Composable
internal fun WatchlistRoute(
    onNavigateToQuote: (String) -> Unit,
    onShowSnackbar: suspend (String, String?, SnackbarDuration) -> Boolean,
    watchlistViewModel: WatchlistViewModel = hiltViewModel()
) {
    val watchlist by watchlistViewModel.displayedWatchlist.collectAsStateWithLifecycle()
    WatchlistScreen(
        watchList = watchlist,
        onNavigateToQuote = onNavigateToQuote,
        onShowSnackbar = onShowSnackbar,
        deleteFromWatchlist = watchlistViewModel::deleteFromWatchlist,
        clearWatchlist = watchlistViewModel::clearWatchlist
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun WatchlistScreen(
    watchList: Result<List<SimpleQuoteData>, DataError.Network>,
    onNavigateToQuote: (String) -> Unit,
    onShowSnackbar: suspend (String, String?, SnackbarDuration) -> Boolean,
    deleteFromWatchlist: (String) -> Unit,
    clearWatchlist: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    when (watchList) {
        is Result.Loading -> {
            WatchlistSkeleton()
        }

        is Result.Error -> {
            WatchlistSkeleton()

            LaunchedEffect(watchList.error) {
                onShowSnackbar(
                    watchList.error.asUiText().asString(context),
                    UiText.StringResource(R.string.feature_watchlist_dismiss).asString(context),
                    SnackbarDuration.Short
                )
            }
        }

        is Result.Success -> {

            if (watchList.data.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
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
                return
            }

            val bottomSheetScaffoldState = rememberBottomSheetScaffoldState()
            var quote by remember { mutableStateOf(watchList.data.first()) }
            var isBottomSheetExpanded by remember { mutableStateOf(false) }

            LaunchedEffect(bottomSheetScaffoldState.bottomSheetState) {
                snapshotFlow { bottomSheetScaffoldState.bottomSheetState.targetValue.ordinal }
                    .collect { state ->
                        isBottomSheetExpanded = (state != 2)
                    }
            }

            val fabPadding by animateDpAsState(
                targetValue = if (isBottomSheetExpanded) 128.dp else 64.dp,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                label = "fabPadding"
            )

            BottomSheetScaffold(
                sheetContent = {
                    QuoteSneakPeek(
                        quote = quote,
                        deleteFromWatchlist = deleteFromWatchlist,
                        modifier = Modifier.clickable { onNavigateToQuote(quote.symbol) }
                    )
                },
                scaffoldState = bottomSheetScaffoldState
            ) {
                Scaffold(
                    floatingActionButton = {
                        ClearWatchlistFab(
                            fabPadding = fabPadding,
                            clearWatchlist = clearWatchlist,
                        )
                    },
                ) { padding ->
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(
                            items = watchList.data,
                            key = { quote -> quote.symbol }
                        ) { item ->
                            WatchlistQuote(
                                quote = item,
                                onNavigateToQuote = onNavigateToQuote,
                                deleteFromWatchList = deleteFromWatchlist,
                                onClick = {
                                    quote = item
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
private fun WatchlistQuote(
    quote: SimpleQuoteData,
    onNavigateToQuote: (String) -> Unit,
    deleteFromWatchList: (String) -> Unit,
    onClick: () -> Unit
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    val maxDragDistance = 500f
    val dragProportion = abs(offsetX) / maxDragDistance
    val weight by animateFloatAsState(
        targetValue = (dragProportion.pow(2)).coerceIn(0.00001f, 1f),
        label = "weight"
    )

    // Keep track of tap gesture
    var isTapped by remember { mutableStateOf(false) }
    LaunchedEffect(isTapped) {
        delay(300)
        isTapped = false
    }

    Row(
        modifier = Modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        // Workaround for double tap gesture without the delay
                        if (isTapped) {
                            onNavigateToQuote(quote.symbol)
                        } else {
                            onClick()
                            isTapped = true
                        }
                    },
                    onLongPress = {
                        onNavigateToQuote(quote.symbol)
                    }
                )
            }
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onHorizontalDrag = { _, dragAmount ->
                        // Update offsetX only if dragAmount is positive or already dragging
                        if (dragAmount > 0 || offsetX != 0f) {
                            offsetX += dragAmount
                            offsetX = offsetX.coerceIn(-maxDragDistance, maxDragDistance)
                        }
                    },
                    onDragEnd = {
                        if (abs(offsetX) > maxDragDistance * 0.75) {
                            deleteFromWatchList(quote.symbol)
                        }
                        offsetX = 0f
                    }
                )
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(weight)
                .fillMaxHeight()
                .padding(vertical = 4.dp)
                .clip(RoundedCornerShape(25))
                .background(MaterialTheme.colorScheme.errorContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = VxmIcons.DeleteSweep,
                contentDescription = stringResource(id = R.string.feature_watchlist_delete),
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.alpha(1f - weight)
            )
        }
        Box(
            modifier = Modifier
                .weight((1f - weight).coerceAtLeast(0.01f))
                .fillMaxHeight()
                .padding(8.dp)
                .clip(RoundedCornerShape(25))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = quote.symbol,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.inverseOnSurface,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .clip(RoundedCornerShape(25))
                        .background(MaterialTheme.colorScheme.inverseSurface)
                        .padding(4.dp)
                        .weight(.2f)
                )
                Text(
                    text = quote.name,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.Center,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .weight(.6f)
                )
                Text(
                    text = String.format(Locale.US, "%.2f", quote.price),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = if (quote.change.startsWith("-")) getNegativeTextColor() else getPositiveTextColor(),
                    maxLines = 1,
                    modifier = Modifier
                        .weight(.2f)
                        .wrapContentSize()
                        .padding(4.dp)
                )
            }
        }
    }
}

@Composable
private fun WatchlistSkeleton() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(12) {
            Row(
                modifier = Modifier
                    .height(60.dp)
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(15))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                // skeleton
            }
        }
    }
}

@ThemePreviews
@Composable
private fun PreviewWatchlistScreen() {
    VxmTheme {
        WatchlistScreen(
            watchList = Result.Success(
                listOf(
                    SimpleQuoteData(
                        symbol = "AAPL",
                        name = "Apple Inc.",
                        price = 145.86,
                        change = "+0.12",
                        percentChange = "+0.08%",
                        logo = "https://logo.clearbit.com/apple.com"
                    ),
                    SimpleQuoteData(
                        symbol = "TSLA",
                        name = "Tesla Inc.",
                        price = 1145.86,
                        change = "-0.12",
                        percentChange = "-0.08%",
                        logo = "https://logo.clearbit.com/tesla.com"
                    ),
                    SimpleQuoteData(
                        symbol = "NVDIA",
                        name = "NVIDIA Inc.",
                        price = 145.86,
                        change = "+0.12",
                        percentChange = "+0.08%",
                        logo = "https://logo.clearbit.com/nvidia.com"
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
            WatchlistSkeleton()
        }
    }
}