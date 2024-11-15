package com.verdenroz.verdaxmarket.feature.quotes.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.verdenroz.verdaxmarket.core.common.error.DataError
import com.verdenroz.verdaxmarket.core.common.result.Result
import com.verdenroz.verdaxmarket.core.designsystem.components.VxmAddIconButton
import com.verdenroz.verdaxmarket.core.designsystem.components.VxmDeleteIconButton
import com.verdenroz.verdaxmarket.core.designsystem.components.VxmTopBar
import com.verdenroz.verdaxmarket.core.designsystem.theme.ThemePreviews
import com.verdenroz.verdaxmarket.core.designsystem.theme.VxmTheme
import com.verdenroz.verdaxmarket.core.model.SimpleQuoteData
import com.verdenroz.verdaxmarket.feature.quotes.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun QuoteTopBar(
    symbol: String,
    quote: Result<SimpleQuoteData, DataError.Network>,
    isWatchlisted: Boolean,
    onNavigateBack: () -> Unit,
    addToWatchlistLocal: (SimpleQuoteData) -> Unit,
    addToWatchlistNetwork: () -> Unit,
    deleteFromWatchlist: () -> Unit,
) {
    VxmTopBar(
        title = {
            Text(
                text = symbol,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.inverseOnSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .clip(RoundedCornerShape(25))
                    .background(MaterialTheme.colorScheme.inverseSurface)
                    .padding(4.dp)
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    tint = MaterialTheme.colorScheme.onSurface,
                    contentDescription = stringResource(id = R.string.feature_quotes_back),
                )
            }
        },
        actions = {
            when (quote) {
                is Result.Success -> {
                    // Can add to watchlist locally on success
                    if (isWatchlisted) {
                        VxmDeleteIconButton(
                            onClick = deleteFromWatchlist,
                        )
                    } else {
                        VxmAddIconButton(
                            onClick = { addToWatchlistLocal(quote.data) },
                        )
                    }
                }

                is Result.Error -> {
                    // Add to watchlist network call on error
                    if (isWatchlisted) {
                        VxmDeleteIconButton(
                            onClick = deleteFromWatchlist,
                        )
                    } else {
                        VxmAddIconButton(
                            onClick = { addToWatchlistNetwork() },
                        )
                    }
                }

                else -> {
                    // Show no icon button on loading
                }
            }
        }
    )
}

@ThemePreviews
@Composable
private fun PreviewStockTopBar() {
    VxmTheme {
        Surface {
            QuoteTopBar(
                symbol = "AAPL",
                quote = Result.Success(
                    SimpleQuoteData(
                        symbol = "AAPL",
                        name = "Apple Inc.",
                        price = 145.12,
                        change = "+0.12",
                        percentChange = "+0.12%",
                        logo = "https://logo.clearbit.com/apple.com",
                    )
                ),
                isWatchlisted = true,
                onNavigateBack = {},
                addToWatchlistLocal = { Result.Success(Unit) },
                addToWatchlistNetwork = { Result.Success(Unit) },
                deleteFromWatchlist = { Result.Success(Unit) },
            )
        }
    }
}