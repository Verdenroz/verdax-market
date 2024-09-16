package com.verdenroz.verdaxmarket.feature.quotes.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.verdenroz.verdaxmarket.core.common.error.DataError
import com.verdenroz.verdaxmarket.core.common.result.Result
import com.verdenroz.verdaxmarket.core.designsystem.theme.ThemePreviews
import com.verdenroz.verdaxmarket.core.designsystem.theme.VxmTheme
import com.verdenroz.verdaxmarket.core.designsystem.util.UiText
import com.verdenroz.verdaxmarket.core.designsystem.util.asUiText
import com.verdenroz.verdaxmarket.core.model.SimpleQuoteData
import com.verdenroz.verdaxmarket.feature.quotes.R


@Composable
fun SimilarQuoteFeed(
    symbol: String,
    similarQuotes: Result<List<SimpleQuoteData>, DataError.Network>,
    navController: NavController,
    snackbarHost: SnackbarHostState,
) {
    val context = LocalContext.current
    when (similarQuotes) {
        is Result.Loading -> {
            SimilarStockFeedSkeleton()
        }

        is Result.Error -> {
            SimilarStockFeedSkeleton()

            LaunchedEffect(similarQuotes.error) {
                snackbarHost.showSnackbar(
                    message = similarQuotes.error.asUiText().asString(context),
                    actionLabel = UiText.StringResource(R.string.feature_quotes_dismiss).asString(context),
                    duration = SnackbarDuration.Short
                )
            }
        }

        is Result.Success -> {
            if (similarQuotes.data.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(id = R.string.feature_quotes_similar) + ": $symbol",
                        style = MaterialTheme.typography.titleMedium,
                        letterSpacing = 1.25.sp,
                        modifier = Modifier.padding(8.dp)
                    )
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        items(
                            items = similarQuotes.data,
                            key = { stock -> stock.symbol }
                        ) {
                            QuoteCard(
                                quote = it,
                                navController = navController
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SimilarStockFeedSkeleton(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.surfaceContainer
) {
    LazyRow {
        repeat(5) {
            item(key = it) {
                Card(
                    modifier = modifier.size(width = 125.dp, height = 75.dp),
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
private fun PreviewSimilarStockFeed() {
    VxmTheme {

        SimilarQuoteFeed(
            symbol = "AAPL",
            similarQuotes = Result.Success(
                listOf(
                    SimpleQuoteData(
                        symbol = "AAPL",
                        name = "Apple Inc.",
                        price = 145.12,
                        change = "+0.12",
                        percentChange = "+0.12%",
                        logo = "https://logo.clearbit.com/apple.com",
                    ),
                    SimpleQuoteData(
                        symbol = "GOOGL",
                        name = "Alphabet Inc.",
                        price = 145.12,
                        change = "+0.12",
                        percentChange = "+0.12%",
                        logo = "https://logo.clearbit.com/google.com",
                    ),
                    SimpleQuoteData(
                        symbol = "MSFT",
                        name = "Microsoft Corporation",
                        price = 145.12,
                        change = "+0.12",
                        percentChange = "+0.12%",
                        logo = "https://logo.clearbit.com/microsoft.com",
                    )
                ),
            ),
            navController = rememberNavController(),
            snackbarHost = SnackbarHostState()
        )
    }
}