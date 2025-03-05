package com.verdenroz.verdaxmarket.feature.search.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.verdenroz.verdaxmarket.core.designsystem.components.VxmAddIconButton
import com.verdenroz.verdaxmarket.core.designsystem.components.VxmAsyncImage
import com.verdenroz.verdaxmarket.core.designsystem.components.VxmDeleteIconButton
import com.verdenroz.verdaxmarket.core.designsystem.theme.ThemePreviews
import com.verdenroz.verdaxmarket.core.designsystem.theme.VxmTheme
import com.verdenroz.verdaxmarket.core.designsystem.theme.getNegativeBackgroundColor
import com.verdenroz.verdaxmarket.core.designsystem.theme.getNegativeTextColor
import com.verdenroz.verdaxmarket.core.designsystem.theme.getPositiveBackgroundColor
import com.verdenroz.verdaxmarket.core.designsystem.theme.getPositiveTextColor
import com.verdenroz.verdaxmarket.core.model.SimpleQuoteData
import com.verdenroz.verdaxmarket.feature.search.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
internal fun RecentQuotes(
    recentQuotes: List<SimpleQuoteData>,
    recentQuotesInWatchlist: Map<String, Boolean>,
    removeQuote: (String) -> Unit,
    onNavigateToQuote: (String) -> Unit,
    clearAll: () -> Unit,
    addToWatchlist: (String, String, String?) -> Unit,
    deleteFromWatchlist: (String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.feature_search_recent_quotes),
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = stringResource(id = R.string.feature_search_clear),
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.clickable(onClick = {
                    if (recentQuotes.isNotEmpty()) {
                        showDialog = true
                    }
                })
            )
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            items(
                recentQuotes,
                key = { it.symbol }
            ) { query ->
                RecentQuoteBody(
                    quote = query,
                    isInWatchlist = recentQuotesInWatchlist[query.symbol] == true,
                    onClick = { onNavigateToQuote(query.symbol) },
                    removeQuote = removeQuote,
                    addToWatchlist = addToWatchlist,
                    deleteFromWatchlist = deleteFromWatchlist
                )
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    clearAll()
                    showDialog = false
                }) {
                    Text(
                        text = stringResource(id = R.string.feature_search_clear),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(
                        text = stringResource(id = R.string.feature_search_cancel_query),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            title = {
                Text(text = stringResource(id = R.string.feature_search_clear_quotes))
            },
            text = {
                Text(text = stringResource(id = R.string.feature_search_remove_query_prompt))
            },
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            textContentColor = MaterialTheme.colorScheme.onSurface,
        )
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun RecentQuoteBody(
    quote: SimpleQuoteData,
    isInWatchlist: Boolean,
    onClick: () -> Unit,
    removeQuote: (String) -> Unit,
    addToWatchlist: (String, String, String?) -> Unit,
    deleteFromWatchlist: (String) -> Unit
) {
    val scope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }
    var isHovering by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .combinedClickable(
                onClick = onClick,
                onLongClick = {
                    scope.launch {
                        isHovering = true
                        showDialog = true
                        delay(2000)
                        isHovering = false
                    }
                },
                onDoubleClick = { showDialog = true }
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Leading content
            if (quote.logo != null) {
                VxmAsyncImage(
                    model = quote.logo!!,
                    description = stringResource(id = R.string.feature_search_logo_description),
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = quote.symbol,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.25.sp,
                        maxLines = 1,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }

            // Main content
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .weight(.5f)
            ) {
                Text(
                    text = quote.symbol,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.25.sp,
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = quote.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = MaterialTheme.typography.headlineSmall.fontWeight,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Trailing content
            Row(
                modifier = Modifier
                    .weight(.6f)
                    .padding(start = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                RotatingText(quote)

                if (isInWatchlist) {
                    VxmDeleteIconButton(
                        onClick = { deleteFromWatchlist(quote.symbol) },
                    )
                } else {
                    VxmAddIconButton(
                        onClick = { addToWatchlist(quote.symbol, quote.name, quote.logo) }
                    )
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                if (!isHovering) {
                    showDialog = false
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    removeQuote(quote.symbol)
                    showDialog = false
                }) {
                    Text(
                        text = stringResource(id = R.string.feature_search_remove_query),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(
                        text = stringResource(id = R.string.feature_search_cancel_query),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            title = {
                Text(text = quote.symbol)
            },
            text = {
                Text(text = stringResource(id = R.string.feature_search_remove_query_prompt))
            },
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            textContentColor = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun RowScope.RotatingText(quote: SimpleQuoteData) {
    var currentIndex by remember { mutableIntStateOf(0) }
    val items = listOf(quote.price.toString(), quote.change, quote.percentChange)

    LaunchedEffect(Unit) {
        while (true) {
            delay(5000)
            currentIndex = (currentIndex + 1) % items.size
        }
    }

    Text(
        text = items[currentIndex],
        style = MaterialTheme.typography.labelLarge,
        color = if (quote.change.toDouble() > 0) getPositiveTextColor() else getNegativeTextColor(),
        textAlign = TextAlign.Center,
        modifier = Modifier
            .clip(RoundedCornerShape(25))
            .background(if (quote.change.toDouble() > 0) getPositiveBackgroundColor() else getNegativeBackgroundColor())
            .padding(4.dp)
            .clickable {
                currentIndex = (currentIndex + 1) % items.size
            }
    )
}

@ThemePreviews
@Composable
private fun PreviewRecentQuotes() {
    VxmTheme {
        val positveRecentQuote = SimpleQuoteData(
            symbol = "AAPL",
            name = "Apple Inc.",
            price = "123.45",
            change = "+0.12",
            percentChange = "+0.12%",
            logo = null,
        )
        val negativeRecentQuote = SimpleQuoteData(
            symbol = "TSLA",
            name = "Apple Inc.",
            price = "123.45",
            change = "-0.12",
            percentChange = "-0.12%",
            logo = null,
        )
        Surface(Modifier.fillMaxSize()) {
            RecentQuotes(
                recentQuotes = listOf(positveRecentQuote, negativeRecentQuote).shuffled(),
                recentQuotesInWatchlist = mapOf(
                    positveRecentQuote.symbol to true,
                    negativeRecentQuote.symbol to false
                ),
                onNavigateToQuote = { },
                removeQuote = { },
                clearAll = { },
                addToWatchlist = { _, _, _ -> },
                deleteFromWatchlist = { }
            )
        }
    }
}

@Composable
internal fun RecentQuotesSkeleton(symbolNames: List<Triple<String, String, String?>>) {
    Column(
        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.feature_search_recent_quotes),
                style = MaterialTheme.typography.titleSmall
            )
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            items(symbolNames) { (symbol, name, logo) ->
                RecentQuoteSkeleton(symbol, name, logo)
            }
        }
    }
}

@Composable
private fun RecentQuoteSkeleton(
    symbol: String,
    name: String,
    logo: String?
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (logo != null) {
            VxmAsyncImage(
                model = logo,
                description = stringResource(id = R.string.feature_search_logo_description),
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = symbol,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.25.sp,
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }

        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .weight(.5f)
        ) {
            Text(
                text = symbol,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.25.sp,
                maxLines = 1,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = MaterialTheme.typography.headlineSmall.fontWeight,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@ThemePreviews
@Composable
private fun PreviewRecentQueriesSkeleton() {
    VxmTheme {
        Surface(Modifier.fillMaxSize()) {
            RecentQuotesSkeleton(
                listOf(
                    Triple("AAPL", "Apple Inc.", null),
                    Triple("TSLA", "Tesla Inc.", null),
                    Triple("GOOGL", "Alphabet Inc.", null),
                    Triple("AMZN", "Amazon.com Inc.", null),
                    Triple("MSFT", "Microsoft Corporation", null),
                    Triple("FB", "Meta Platforms Inc.", null),
                    Triple("NVDA", "NVIDIA Corporation", null),
                    Triple("PYPL", "PayPal Holdings Inc.", null),
                    Triple("INTC", "Intel Corporation", null),
                    Triple("CSCO", "Cisco Systems Inc.", null),
                )
            )
        }
    }
}