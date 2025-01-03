package com.verdenroz.verdaxmarket.feature.quotes.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.verdenroz.verdaxmarket.core.designsystem.components.VxmAsyncImage
import com.verdenroz.verdaxmarket.core.designsystem.theme.ThemePreviews
import com.verdenroz.verdaxmarket.core.designsystem.theme.VxmTheme
import com.verdenroz.verdaxmarket.core.designsystem.theme.getNegativeTextColor
import com.verdenroz.verdaxmarket.core.designsystem.theme.getPositiveTextColor
import com.verdenroz.verdaxmarket.feature.quotes.R
import java.util.Locale

/**
 * A composable that displays the headline of a stock quote.
 * @param name The name of the stock.
 * @param symbol The symbol of the stock.
 * @param price The price of the stock.
 * @param change The change in the price of the stock.
 * @param percentChange The percentage change in the price of the stock.
 * @param afterHoursPrice The after hours price of the stock.
 * @param logo The url to the logo of the stock.
 */
@Composable
internal fun QuoteHeadline(
    name: String,
    symbol: String,
    price: Double,
    change: String,
    percentChange: String,
    preMarketPrice: Double?,
    afterHoursPrice: Double?,
    logo: String?
) {
    ListItem(
        overlineContent = {
            Text(
                text = name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        headlineContent = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = String.format(Locale.US, "%.2f", price),
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Black,
                )
                Text(
                    text = change,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = let { if (change.contains("-")) getNegativeTextColor() else getPositiveTextColor() },
                )
                Text(
                    text = percentChange,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = let { if (percentChange.contains("-")) getNegativeTextColor() else getPositiveTextColor() },
                )
            }
        },
        supportingContent = {
            if (preMarketPrice != null || afterHoursPrice != null) {
                val displayPrice = preMarketPrice ?: afterHoursPrice
                val priceDifference = displayPrice?.minus(price)
                val percentageDifference = (priceDifference ?: 0.0) / price * 100

                if (priceDifference == null) {
                    return@ListItem
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.feature_quotes_after_hours) + displayPrice,
                        style = MaterialTheme.typography.labelLarge,
                    )
                    Text(
                        text = String.format(
                            Locale.US,
                            "%.2f",
                            priceDifference
                        ),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = if (priceDifference < 0) getNegativeTextColor() else getPositiveTextColor(),
                    )
                    Text(
                        text = String.format(
                            Locale.US,
                            "(%.2f%%)",
                            percentageDifference
                        ),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = if (priceDifference < 0) getNegativeTextColor() else getPositiveTextColor(),
                    )
                }
            }
        },
        trailingContent = {
            if (logo != null) {
                VxmAsyncImage(
                    model = logo,
                    description = stringResource(id = R.string.feature_quotes_logo_description),
                    modifier = Modifier
                        .size(64.dp)
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
private fun PreviewStockHeadline() {
    VxmTheme {
        QuoteHeadline(
            name = "Apple Inc.",
            symbol = "AAPL",
            price = 145.86,
            change = "-0.14",
            percentChange = "(-0.10%)",
            preMarketPrice = 145.86,
            afterHoursPrice = 145.86,
            logo = "https://logo.clearbit.com/apple.com"
        )
    }
}


