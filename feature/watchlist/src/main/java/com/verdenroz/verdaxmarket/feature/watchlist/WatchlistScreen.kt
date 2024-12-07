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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.verdenroz.verdaxmarket.core.designsystem.components.VxmAsyncImage
import com.verdenroz.verdaxmarket.core.designsystem.icons.VxmIcons
import com.verdenroz.verdaxmarket.core.designsystem.theme.ThemePreviews
import com.verdenroz.verdaxmarket.core.designsystem.theme.VxmTheme
import com.verdenroz.verdaxmarket.core.designsystem.theme.getNegativeTextColor
import com.verdenroz.verdaxmarket.core.designsystem.theme.getPositiveTextColor
import com.verdenroz.verdaxmarket.core.designsystem.util.UiText
import com.verdenroz.verdaxmarket.core.designsystem.util.asUiText
import com.verdenroz.verdaxmarket.core.model.WatchlistQuote
import com.verdenroz.verdaxmarket.feature.watchlist.components.ClearWatchlistFab
import com.verdenroz.verdaxmarket.feature.watchlist.components.QuoteSneakPeek
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.pow

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

    when (watchlistState) {
        is WatchlistState.Loading -> {
            WatchlistSkeleton(watchlist)
        }

        is WatchlistState.Error -> {
            WatchlistSkeleton(watchlist)

            LaunchedEffect(watchlistState.error) {
                onShowSnackbar(
                    watchlistState.error.asUiText().asString(context),
                    UiText.StringResource(R.string.feature_watchlist_dismiss).asString(context),
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
                return
            }

            val bottomSheetScaffoldState = rememberBottomSheetScaffoldState()
            var quote by remember { mutableStateOf(watchlistState.data.values.first()) }
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
                            // The watchlist quote is has been loaded from the socket
                            val loadedQuote = watchlistState.data[watchlistQuote.symbol]
                            if (loadedQuote != null) {
                                WatchlistQuote(
                                    quote = loadedQuote,
                                    onNavigateToQuote = onNavigateToQuote,
                                    deleteFromWatchList = deleteFromWatchlist,
                                    onClick = {
                                        quote = loadedQuote
                                        scope.launch {
                                            bottomSheetScaffoldState.bottomSheetState.expand()
                                        }
                                    }
                                )
                            } else {
                                // The watchlist quote is still loading
                                WatchlistQuoteSkeleton(watchlistQuote)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WatchlistQuoteSkeleton(quote: WatchlistQuote) {
    Box(
        modifier = Modifier
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
            if (quote.logo != null) {
                VxmAsyncImage(
                    model = quote.logo!!,
                    description = stringResource(id = R.string.feature_watchlist_logo_description),
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.inverseSurface),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = quote.symbol,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.25.sp,
                        maxLines = 1,
                        color = MaterialTheme.colorScheme.inverseOnSurface,
                    )
                }
            }
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
        }
    }
}


@Composable
private fun WatchlistQuote(
    quote: WatchlistQuote,
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
                if (quote.logo != null) {
                    VxmAsyncImage(
                        model = quote.logo!!,
                        description = stringResource(id = R.string.feature_watchlist_logo_description),
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.inverseSurface),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = quote.symbol,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.25.sp,
                            maxLines = 1,
                            color = MaterialTheme.colorScheme.inverseOnSurface,
                        )
                    }
                }
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
                if (quote.change != null) {
                    Text(
                        text = quote.change!!,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (quote.change!!.startsWith("-")) getNegativeTextColor() else getPositiveTextColor(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(.2f)
                            .wrapContentSize()
                            .padding(4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun WatchlistSkeleton(watchlist: List<WatchlistQuote>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(watchlist, key = { it.symbol }) {
            WatchlistQuoteSkeleton(it)
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
                    name = "Tesla Inc.",
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
                        percentChange = "-0.16",
                        logo = null,
                        order = 0
                    ),
                    "TSLA" to WatchlistQuote(
                        symbol = "TSLA",
                        name = "Tesla Inc.",
                        price = "678.90",
                        change = "+0.23",
                        percentChange = "+0.03",
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
                listOf(
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
                        name = "Tesla Inc.",
                        price = "678.90",
                        change = "+0.23",
                        percentChange = "+0.03",
                        logo = null,
                        order = 1
                    ),
                )
            )
        }
    }
}