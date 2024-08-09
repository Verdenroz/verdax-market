package com.verdenroz.verdaxmarket.feature.quotes.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.verdenroz.verdaxmarket.core.designsystem.theme.ThemePreviews
import com.verdenroz.verdaxmarket.core.designsystem.theme.VxmTheme
import com.verdenroz.verdaxmarket.core.designsystem.theme.negativeTextColor
import com.verdenroz.verdaxmarket.core.designsystem.theme.positiveTextColor
import com.verdenroz.verdaxmarket.core.model.SimpleQuoteData
import com.verdenroz.verdaxmarket.feature.quotes.navigation.navigateToQuote

@Composable
internal fun QuoteCard(
    quote: SimpleQuoteData,
    navController: NavController
) {
    Card(
        modifier = Modifier
            .size(width = 125.dp, height = 75.dp)
            .clickable {
                navController.navigateToQuote(quote.symbol)
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.SpaceAround
        ) {
            Text(
                text = quote.name,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = quote.price.toString(),
                style = MaterialTheme.typography.titleSmall
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = quote.change,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (quote.change.contains('+')) positiveTextColor else negativeTextColor
                )
                Text(
                    text = quote.percentChange,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (quote.change.contains('+')) positiveTextColor else negativeTextColor
                )
            }
        }
    }
}

@ThemePreviews
@Composable
private fun PreviewStockCard() {
    VxmTheme {
        QuoteCard(
            quote = SimpleQuoteData(
                symbol = "AAPL",
                name = "Apple Inc.",
                price = 150.0,
                change = "+5.0",
                percentChange = "+5%"
            ),
            navController = rememberNavController()
        )
    }
}