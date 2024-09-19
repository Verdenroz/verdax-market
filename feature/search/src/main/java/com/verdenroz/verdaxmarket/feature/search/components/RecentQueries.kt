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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.verdenroz.verdaxmarket.core.designsystem.theme.ThemePreviews
import com.verdenroz.verdaxmarket.core.designsystem.theme.VxmTheme
import com.verdenroz.verdaxmarket.core.model.RecentSearchQuery
import com.verdenroz.verdaxmarket.feature.search.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun RecentQueries(
    recentQueries: List<RecentSearchQuery>,
    removeRecentQuery: (RecentSearchQuery) -> Unit,
    onClick: (String) -> Unit,
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
                text = stringResource(id = R.string.feature_search_recent_queries),
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = stringResource(id = R.string.feature_search_clear),
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.clickable(onClick = {
                    if (recentQueries.isNotEmpty()) {
                        showDialog = true
                    }
                })
            )
        }

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            recentQueries.forEach { query ->
                RecentQueryText(
                    query = query,
                    onClick = onClick,
                    removeRecentQuery = removeRecentQuery
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
                Text(text = stringResource(id = R.string.feature_search_clear_queries))
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
private fun RecentQueryText(
    query: RecentSearchQuery,
    onClick: (String) -> Unit,
    removeRecentQuery: (RecentSearchQuery) -> Unit
) {
    val scope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }
    var isHovering by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .combinedClickable(
                onClick = { onClick(query.query) },
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
        Text(
            text = query.query,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(8.dp)
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
                    removeRecentQuery(query)
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
                Text(text = query.query)
            },
            text = {
                Text(text = stringResource(id = R.string.feature_search_remove_query_prompt))
            },
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            textContentColor = MaterialTheme.colorScheme.onSurface,
        )
    }

}

@ThemePreviews
@Composable
private fun PreviewRecentQueries() {
    VxmTheme {
        Surface(Modifier.fillMaxSize()) {
            RecentQueries(
                recentQueries = listOf(
                    RecentSearchQuery("AAPL"),
                    RecentSearchQuery("TSLA"),
                    RecentSearchQuery("AMZN"),
                    RecentSearchQuery("GOOGL"),
                    RecentSearchQuery("MSFT"),
                    RecentSearchQuery("FB"),
                    RecentSearchQuery("NVDA"),
                    RecentSearchQuery("INTC"),
                    RecentSearchQuery("AMD"),
                    RecentSearchQuery("QCOM"),
                ),
                onClick = { },
                removeRecentQuery = { },
                clearAll = { }
            )
        }
    }
}