package com.verdenroz.verdaxmarket.feature.quotes.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.verdenroz.verdaxmarket.core.designsystem.theme.ThemePreviews
import com.verdenroz.verdaxmarket.core.designsystem.theme.VxmTheme
import com.verdenroz.verdaxmarket.core.model.SimpleQuoteData
import com.verdenroz.verdaxmarket.feature.quotes.R


@Composable
fun SimilarQuoteFeed(
    symbol: String,
    similarQuotes: List<SimpleQuoteData>,
    onNavigateToQuote: (String) -> Unit,
) {
    if (similarQuotes.isNotEmpty()) {
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
                    items = similarQuotes,
                    key = { quote -> quote.symbol }
                ) {
                    QuoteCard(
                        quote = it,
                        onNavigateToQuote = onNavigateToQuote
                    )
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
            similarQuotes =
            listOf(
                SimpleQuoteData(
                    symbol = "AAPL",
                    name = "Apple Inc.",
                    price = "145.12",
                    change = "+0.12",
                    percentChange = "+0.12%",
                    logo = "https://logo.clearbit.com/apple.com",
                ),
                SimpleQuoteData(
                    symbol = "GOOGL",
                    name = "Alphabet Inc.",
                    price = "145.12",
                    change = "+0.12",
                    percentChange = "+0.12%",
                    logo = "https://logo.clearbit.com/google.com",
                ),
                SimpleQuoteData(
                    symbol = "MSFT",
                    name = "Microsoft Corporation",
                    price = "145.12",
                    change = "+0.12",
                    percentChange = "+0.12%",
                    logo = "https://logo.clearbit.com/microsoft.com",
                )
            ),
            onNavigateToQuote = {}
        )
    }
}