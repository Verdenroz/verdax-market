package com.verdenroz.verdaxmarket.feature.watchlist.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import com.verdenroz.verdaxmarket.core.designsystem.components.VxmAddIconButton
import com.verdenroz.verdaxmarket.core.designsystem.components.VxmDeleteIconButton
import com.verdenroz.verdaxmarket.core.designsystem.theme.ThemePreviews
import com.verdenroz.verdaxmarket.core.designsystem.theme.VxmTheme
import com.verdenroz.verdaxmarket.core.designsystem.theme.negativeTextColor
import com.verdenroz.verdaxmarket.core.designsystem.theme.positiveTextColor
import com.verdenroz.verdaxmarket.core.model.SimpleQuoteData
import java.util.Locale

@Composable
internal fun QuoteSneakPeek(
    quote: SimpleQuoteData,
    addToWatchlist: (SimpleQuoteData) -> Unit,
    deleteFromWatchlist: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isWatchlisted by remember { mutableStateOf(true) }

    ListItem(
        modifier = modifier,
        overlineContent = {
            Text(
                text = quote.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        headlineContent = {
            Row(
                modifier = Modifier.fillMaxWidth(.9f),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = String.format(Locale.US, "%.2f", quote.price),
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Black,
                )
                Text(
                    text = quote.change,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = let { if (quote.change.startsWith("-")) negativeTextColor else positiveTextColor },
                )
                Text(
                    text = quote.percentChange,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = let { if (quote.percentChange.startsWith("-")) negativeTextColor else positiveTextColor },
                )
            }
        },
        trailingContent = {
            val onClick = {
                isWatchlisted = !isWatchlisted
                if (isWatchlisted) {
                    deleteFromWatchlist(quote.symbol)
                } else {
                    addToWatchlist(quote)
                }
            }
            SmallFloatingActionButton(
                onClick = onClick,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
            ) {
                if (isWatchlisted) {
                    VxmDeleteIconButton(
                        onClick = onClick
                    )
                } else {
                    VxmAddIconButton(
                        onClick = onClick
                    )
                }
            }
        },
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.primary,
            headlineColor = MaterialTheme.colorScheme.onPrimary,
            overlineColor = MaterialTheme.colorScheme.onPrimary,
            supportingColor = MaterialTheme.colorScheme.onPrimary,
        )
    )
    HorizontalDivider(
        thickness = Dp.Hairline,
        color = MaterialTheme.colorScheme.outline,
        modifier = Modifier.fillMaxWidth()
    )
}

@ThemePreviews
@Composable
private fun PreviewBottomSheetContent() {
    VxmTheme {
        QuoteSneakPeek(
            quote = SimpleQuoteData(
                symbol = "AAPL",
                name = "Apple Inc.",
                price = 145.12,
                change = "+0.12",
                percentChange = "+0.12%",
                logo = "https://logo.clearbit.com/https://www.apple.com"
            ),
            addToWatchlist = {},
            deleteFromWatchlist = {},
        )
    }
}