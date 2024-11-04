package com.verdenroz.verdaxmarket.feature.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.verdenroz.verdaxmarket.core.common.error.DataError
import com.verdenroz.verdaxmarket.core.common.result.Result
import com.verdenroz.verdaxmarket.core.designsystem.components.VxmTabRowPager
import com.verdenroz.verdaxmarket.core.designsystem.theme.ThemePreviews
import com.verdenroz.verdaxmarket.core.designsystem.theme.VxmTheme
import com.verdenroz.verdaxmarket.core.designsystem.theme.negativeBackgroundColor
import com.verdenroz.verdaxmarket.core.designsystem.theme.negativeTextColor
import com.verdenroz.verdaxmarket.core.designsystem.theme.positiveBackgroundColor
import com.verdenroz.verdaxmarket.core.designsystem.theme.positiveTextColor
import com.verdenroz.verdaxmarket.core.designsystem.util.UiText
import com.verdenroz.verdaxmarket.core.designsystem.util.asUiText
import com.verdenroz.verdaxmarket.core.model.MarketMover
import com.verdenroz.verdaxmarket.feature.home.R
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun MarketMovers(
    listState: LazyListState,
    actives: Result<List<MarketMover>, DataError.Network>,
    losers: Result<List<MarketMover>, DataError.Network>,
    gainers: Result<List<MarketMover>, DataError.Network>,
    onNavigateToQuote: (String) -> Unit,
    onShowSnackbar: suspend (String, String?, SnackbarDuration) -> Boolean,
) {
    val state = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()
    val tabTitles = listOf(
        stringResource(id = R.string.feature_home_mostActive),
        stringResource(id = R.string.feature_home_topGainers),
        stringResource(id = R.string.feature_home_topLosers)
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 300.dp, max = 600.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        VxmTabRowPager(
            state = state
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    text = { Text(title) },
                    selected = state.currentPage == index,
                    onClick = {
                        scope.launch {
                            listState.animateScrollToItem(3)
                            state.animateScrollToPage(index)
                        }
                    }
                )
            }
        }
        HorizontalPager(
            state = state,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .fillMaxWidth()
        ) { page ->
            when (page) {
                0 -> {
                    MarketMoversList(
                        quotes = actives,
                        onQuoteClick = onNavigateToQuote,
                        onShowSnackbar = onShowSnackbar,
                    )
                }

                1 -> {
                    MarketMoversList(
                        quotes = gainers,
                        onShowSnackbar = onShowSnackbar,
                        onQuoteClick = onNavigateToQuote,
                    )
                }

                2 -> {
                    MarketMoversList(
                        quotes = losers,
                        onShowSnackbar = onShowSnackbar,
                        onQuoteClick = onNavigateToQuote,
                    )
                }
            }
        }
    }
}

@Composable
fun MarketMoversList(
    quotes: Result<List<MarketMover>, DataError.Network>,
    onQuoteClick: (String) -> Unit,
    onShowSnackbar: suspend (String, String?, SnackbarDuration) -> Boolean,
) {
    val context = LocalContext.current

    when (quotes) {
        is Result.Loading -> {
            MarketMoversSkeleton()
        }

        is Result.Error -> {
            MarketMoversSkeleton()

            LaunchedEffect(quotes.error) {
                onShowSnackbar(
                    quotes.error.asUiText().asString(context),
                    UiText.StringResource(R.string.feature_home_dismiss).asString(context),
                    SnackbarDuration.Short
                )
            }
        }

        is Result.Success -> {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, start = 8.dp, end = 8.dp)
            ) {
                items(
                    items = quotes.data,
                    key = { quote -> quote.symbol }
                ) { quote ->
                    MarketMoverStock(mover = quote, onNavigateToQuote = onQuoteClick)
                }
            }
        }
    }
}

@Composable
fun MarketMoverStock(
    mover: MarketMover,
    onNavigateToQuote: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(25))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(8.dp)
            .clickable {
                onNavigateToQuote(mover.symbol)
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = mover.symbol,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.inverseOnSurface,
                modifier = Modifier
                    .clip(RoundedCornerShape(25))
                    .background(MaterialTheme.colorScheme.inverseSurface)
                    .padding(4.dp)
            )
            Text(
                text = mover.name,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Light,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = String.format(Locale.US, "%.2f", mover.price.toDouble()),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .weight(1f)
                .wrapContentSize()
                .padding(4.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = mover.change,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = let { if (mover.change.startsWith("-")) negativeTextColor else positiveTextColor },
            modifier = Modifier
                .weight(1f)
                .wrapContentSize()
                .clip(CircleShape)
                .background(
                    if (mover.change.startsWith("-")) negativeBackgroundColor else positiveBackgroundColor
                )
                .padding(4.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = mover.percentChange,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = let { if (mover.change.startsWith("-")) negativeTextColor else positiveTextColor },
            modifier = Modifier
                .weight(1f)
                .wrapContentSize()
                .clip(CircleShape)
                .background(
                    if (mover.change.startsWith("-")) negativeBackgroundColor else positiveBackgroundColor
                )
                .padding(4.dp)
        )
    }
}

@ThemePreviews
@Composable
private fun PreviewMarketMoverStock() {
    VxmTheme {
        MarketMoverStock(
            mover = MarketMover(
                symbol = "AAPL",
                name = "Apple Inc.",
                price = "100.0",
                change = "+100.0",
                percentChange = "+100%"
            ),
            onNavigateToQuote = {}
        )
    }
}

@Composable
fun MarketMoversSkeleton(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.surfaceVariant
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainer)
    ) {
        repeat(5) {
            item(key = it) {
                Card(
                    modifier = modifier
                        .padding(top = 8.dp, start = 8.dp, end = 8.dp)
                        .height(50.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = color)
                ) {
                    // skeleton
                }
            }
        }
    }
}

@ThemePreviews
@Composable
private fun PreviewMarketMoversSkeleton() {
    VxmTheme {
        MarketMoversSkeleton()
    }
}