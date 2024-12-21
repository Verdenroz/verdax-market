package com.verdenroz.verdaxmarket.feature.watchlist.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.verdenroz.verdaxmarket.core.designsystem.components.VxmAsyncImage
import com.verdenroz.verdaxmarket.core.designsystem.components.VxmListItem
import com.verdenroz.verdaxmarket.core.designsystem.icons.VxmIcons
import com.verdenroz.verdaxmarket.core.designsystem.theme.ThemePreviews
import com.verdenroz.verdaxmarket.core.designsystem.theme.VxmTheme
import com.verdenroz.verdaxmarket.core.model.WatchlistQuote
import com.verdenroz.verdaxmarket.feature.watchlist.R

@Composable
internal fun QuoteOptionsPeek(
    quote: WatchlistQuote,
    onNavigateToQuote: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = stringResource(id = R.string.feature_watchlist_confirm_delete_title)) },
            text = {
                Text(
                    text = stringResource(
                        id = R.string.feature_watchlist_confirm_delete_message,
                        quote.symbol
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDialog = false
                    }
                ) {
                    Text(
                        text = stringResource(id = R.string.feature_watchlist_confirm),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDialog = false }
                ) {
                    Text(
                        text = stringResource(id = R.string.feature_watchlist_cancel),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        )
    }

    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!quote.logo.isNullOrBlank()) {
                VxmAsyncImage(
                    model = quote.logo!!,
                    description = stringResource(id = R.string.feature_watchlist_logo_description, quote.name),
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
                        color = MaterialTheme.colorScheme.inverseOnSurface
                    )
                }
            }
            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .weight(1f)
            ) {
                Text(
                    text = quote.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = quote.symbol,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        HorizontalDivider()
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            QuoteOption(
                text = stringResource(id = R.string.feature_watchlist_more_details),
                icon = VxmIcons.Search,
                onClick = onNavigateToQuote
            )
            QuoteOption(
                text = stringResource(id = R.string.feature_watchlist_move_up),
                icon = VxmIcons.KeyboardUp,
                onClick = onMoveUp
            )
            QuoteOption(
                text = stringResource(id = R.string.feature_watchlist_move_down),
                icon = VxmIcons.KeyboardDown,
                onClick = onMoveDown
            )

            HorizontalDivider()

            QuoteOption(
                text = stringResource(id = R.string.feature_watchlist_delete),
                icon = VxmIcons.Remove,
                onClick = { showDialog = true }
            )
        }
    }
}

@Composable
private fun QuoteOption(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    VxmListItem(
        modifier = modifier.clickable { onClick() },
        leadingContent = {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = MaterialTheme.colorScheme.onSurface
            )
        },
        headlineContent = {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    )
}

@ThemePreviews
@Composable
private fun PreviewQuoteOptionsContent() {
    VxmTheme {
        Surface {
            QuoteOptionsPeek(
                quote = WatchlistQuote(
                    symbol = "AAPL",
                    name = "Apple Inc.",
                    price = null,
                    change = null,
                    percentChange = null,
                    logo = null,
                    order = 0
                ),
                onNavigateToQuote = { },
                onMoveUp = { },
                onMoveDown = { },
                onDelete = { },
            )
        }
    }
}