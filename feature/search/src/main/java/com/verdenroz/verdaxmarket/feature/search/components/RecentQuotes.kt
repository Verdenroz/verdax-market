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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.verdenroz.verdaxmarket.core.designsystem.components.VxmAsyncImage
import com.verdenroz.verdaxmarket.core.designsystem.icons.VxmIcons
import com.verdenroz.verdaxmarket.core.designsystem.theme.ThemePreviews
import com.verdenroz.verdaxmarket.core.designsystem.theme.VxmTheme
import com.verdenroz.verdaxmarket.core.designsystem.theme.getNegativeBackgroundColor
import com.verdenroz.verdaxmarket.core.designsystem.theme.getNegativeTextColor
import com.verdenroz.verdaxmarket.core.designsystem.theme.getPositiveBackgroundColor
import com.verdenroz.verdaxmarket.core.designsystem.theme.getPositiveTextColor
import com.verdenroz.verdaxmarket.core.model.RecentQuoteResult
import com.verdenroz.verdaxmarket.feature.search.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

@Composable
internal fun RecentQuotes(
    recentQuotes: List<RecentQuoteResult>,
    recentQuotesInWatchlist: List<Boolean>,
    removeQuote: (RecentQuoteResult) -> Unit,
    onNavigateToQuote: (String) -> Unit,
    clearAll: () -> Unit,
    addToWatchlist: (String) -> Unit,
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
                    isInWatchlist = recentQuotesInWatchlist[recentQuotes.indexOf(query)],
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
    quote: RecentQuoteResult,
    isInWatchlist: Boolean,
    onClick: () -> Unit,
    removeQuote: (RecentQuoteResult) -> Unit,
    addToWatchlist: (String) -> Unit,
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
                modifier = Modifier.padding(horizontal = 16.dp).weight(.4f)
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
                    IconButton(
                        onClick = { deleteFromWatchlist(quote.symbol) },
                    ) {
                        Icon(
                            VxmIcons.Remove,
                            contentDescription = stringResource(id = R.string.feature_search_remove)
                        )
                    }
                } else {
                    IconButton(
                        onClick = { addToWatchlist(quote.symbol) },
                    ) {
                        Icon(
                            VxmIcons.Add,
                            contentDescription = stringResource(id = R.string.feature_search_add)
                        )
                    }
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
                    removeQuote(quote)
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
private fun RowScope.RotatingText(quote: RecentQuoteResult) {
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
private fun PreviewRecentQueries() {
    VxmTheme {
        val positveRecentQuote = RecentQuoteResult(
            symbol = "AAPL",
            name = "Apple Inc.",
            price = "123.45",
            change = "+0.12",
            percentChange = "+0.12%",
            logo = null,
            timestamp = Clock.System.now()
        )
        val negativeRecentQuote = RecentQuoteResult(
            symbol = "TSLA",
            name = "Apple Inc.",
            price = "123.45",
            change = "-0.12",
            percentChange = "-0.12%",
            logo = null,
            timestamp = Clock.System.now()
        )
        Surface(Modifier.fillMaxSize()) {
            RecentQuotes(
                recentQuotes = listOf(positveRecentQuote, negativeRecentQuote).shuffled(),
                recentQuotesInWatchlist = listOf(true, false),
                onNavigateToQuote = { },
                removeQuote = { },
                clearAll = {  },
                addToWatchlist = { },
                deleteFromWatchlist = { }
            )
        }
    }
}