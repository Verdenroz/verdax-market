package com.verdenroz.verdaxmarket.feature.search.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.verdenroz.verdaxmarket.core.designsystem.components.VxmAsyncImage
import com.verdenroz.verdaxmarket.core.designsystem.components.VxmListItem
import com.verdenroz.verdaxmarket.core.designsystem.theme.ThemePreviews
import com.verdenroz.verdaxmarket.core.designsystem.theme.VxmTheme
import com.verdenroz.verdaxmarket.core.designsystem.theme.contrastNegativeTextColor
import com.verdenroz.verdaxmarket.core.designsystem.theme.contrastPositiveTextColor
import com.verdenroz.verdaxmarket.core.designsystem.theme.negativeBackgroundColor
import com.verdenroz.verdaxmarket.core.designsystem.theme.positiveBackgroundColor
import com.verdenroz.verdaxmarket.core.model.RecentQuoteResult
import com.verdenroz.verdaxmarket.feature.quotes.navigation.navigateToQuote
import com.verdenroz.verdaxmarket.feature.search.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun RecentQuotes(
    recentQuotes: List<RecentQuoteResult>,
    navController: NavController,
    removeQuote: (RecentQuoteResult) -> Unit,
    clearAll: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
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

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            recentQuotes.forEach { query ->
                RecentQuoteBody(
                    quote = query,
                    onClick = { navController.navigateToQuote(query.symbol) },
                    removeQuote = removeQuote
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
    onClick: () -> Unit,
    removeQuote: (RecentQuoteResult) -> Unit
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
        VxmListItem(
            headlineContent = {
                Text(
                    text = quote.symbol,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.25.sp,
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            },
            supportingContent = {
                Text(
                    text = quote.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = MaterialTheme.typography.headlineSmall.fontWeight,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            leadingContent = {
                if (quote.logo != null) {
                    VxmAsyncImage(
                        context = LocalContext.current,
                        model = quote.logo!!,
                        description = stringResource(id = R.string.feature_search_cancel),
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
            },
            trailingContent = {
                Row(
                    modifier = Modifier.fillMaxWidth(.6f),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = quote.price.toString(),
                        style = MaterialTheme.typography.labelLarge,
                        color = if (quote.change.toDouble() > 0) contrastPositiveTextColor else contrastNegativeTextColor,
                        modifier = Modifier
                            .clip(RoundedCornerShape(25))
                            .background(if (quote.change.toDouble() > 0) positiveBackgroundColor else negativeBackgroundColor)
                            .padding(4.dp)
                    )
                    RotatingText(quote = quote)
                }
            }
        )
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
private fun RotatingText(quote: RecentQuoteResult) {
    var showChange by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(5000)
            showChange = !showChange
        }
    }

    Text(
        text = if (showChange) quote.change else quote.percentChange,
        style = MaterialTheme.typography.labelLarge,
        color = if (quote.change.toDouble() > 0) contrastPositiveTextColor else contrastNegativeTextColor,
        modifier = Modifier
            .clip(RoundedCornerShape(25))
            .background(if (quote.change.toDouble() > 0) positiveBackgroundColor else negativeBackgroundColor)
            .padding(4.dp)
    )
}

@ThemePreviews
@Composable
private fun PreviewRecentQueries() {
    VxmTheme {
        val positveRecentQuote = RecentQuoteResult(
            symbol = "AAPL",
            name = "Apple Inc.",
            price = 123.45,
            change = "+0.12",
            percentChange = "+0.12%",
            logo = null,
            timestamp = Clock.System.now()
        )
        val negativeRecentQuote = RecentQuoteResult(
            symbol = "AAPL",
            name = "Apple Inc.",
            price = 123.45,
            change = "-0.12",
            percentChange = "-0.12%",
            logo = null,
            timestamp = Clock.System.now()
        )
        Surface(Modifier.fillMaxSize()) {
            RecentQuotes(
                recentQuotes = listOf(positveRecentQuote, negativeRecentQuote, positveRecentQuote).shuffled(),
                navController = rememberNavController(),
                removeQuote = { },
                clearAll = {  }
            )
        }
    }
}