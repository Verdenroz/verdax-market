package com.verdenroz.verdaxmarket.feature.quotes.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.verdenroz.verdaxmarket.core.designsystem.components.VxmListItem
import com.verdenroz.verdaxmarket.core.designsystem.theme.ThemePreviews
import com.verdenroz.verdaxmarket.core.designsystem.theme.VxmTheme
import com.verdenroz.verdaxmarket.core.model.FullQuoteData
import com.verdenroz.verdaxmarket.feature.quotes.R
import com.verdenroz.verdaxmarket.feature.quotes.previewFullQuoteData
import java.util.Locale

@Composable
internal fun QuoteSummary(quote: FullQuoteData) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.surfaceContainerLow),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Profile(quote = quote)
        quote.open?.let {
            QuoteDetailCell(label = stringResource(id = R.string.feature_quotes_open)) {
                SimpleDetailText(
                    text = quote.open.toString()
                )
            }
        }
        if (quote.low != null && quote.high != null)
            QuoteDetailCell(
                label = stringResource(id = R.string.feature_quotes_days_range),
            ) {
                PriceRangeLine(
                    low = quote.low!!,
                    high = quote.high!!,
                    current = quote.price
                )
            }
        if (quote.yearLow != null && quote.yearHigh != null)
            QuoteDetailCell(
                label = stringResource(id = R.string.feature_quotes_fifty_two_week_range),
            ) {
                PriceRangeLine(
                    low = quote.yearLow!!,
                    high = quote.yearHigh!!,
                    current = quote.price
                )
            }
        if (quote.volume != null)
            QuoteDetailCell(
                label = stringResource(id = R.string.feature_quotes_volume),
            ) {
                SimpleDetailText(text = quote.volume.toString())
            }
        if (quote.avgVolume != null)
            QuoteDetailCell(
                label = stringResource(id = R.string.feature_quotes_avg_volume),
            ) {
                SimpleDetailText(text = quote.avgVolume.toString())
            }

        quote.marketCap?.let {
            QuoteDetailCell(
                label = stringResource(id = R.string.feature_quotes_market_cap),
            ) {
                SimpleDetailText(text = it)
            }
        }
        quote.netAssets?.let {
            QuoteDetailCell(
                label = stringResource(id = R.string.feature_quotes_net_assets),
            ) {
                SimpleDetailText(text = it)
            }
        }
        quote.nav?.let {
            QuoteDetailCell(
                label = stringResource(id = R.string.feature_quotes_nav),
            ) {
                SimpleDetailText(text = it.toString())
            }
        }
        quote.pe?.let {
            QuoteDetailCell(
                label = stringResource(id = R.string.feature_quotes_pe_ratio),
            ) {
                SimpleDetailText(text = it)
            }
        }
        quote.eps?.let {
            QuoteDetailCell(
                label = stringResource(id = R.string.feature_quotes_eps),
            ) {
                SimpleDetailText(text = it)
            }
        }
        quote.beta?.let {
            QuoteDetailCell(
                label = stringResource(id = R.string.feature_quotes_beta),
            ) {
                SimpleDetailText(text = it)
            }
        }

        quote.expenseRatio?.let {
            QuoteDetailCell(
                label = stringResource(id = R.string.feature_quotes_expense_ratio),
            ) {
                SimpleDetailText(text = it)
            }
        }

        quote.dividend?.let {
            QuoteDetailCell(
                label = stringResource(id = R.string.feature_quotes_dividend_yield),
            ) {
                SimpleDetailText(text = it + " (" + quote.yield + ")")
            }
        }

        quote.lastDividend?.let {
            QuoteDetailCell(
                label = stringResource(id = R.string.feature_quotes_last_dividend),
            ) {
                SimpleDetailText(text = it)
            }
        }

        quote.exDividend?.let {
            QuoteDetailCell(
                label = stringResource(id = R.string.feature_quotes_ex_dividend),
            ) {
                SimpleDetailText(text = it)
            }
        }

        quote.lastCapitalGain?.let {
            QuoteDetailCell(
                label = stringResource(id = R.string.feature_quotes_last_capital_gain),
            ) {
                SimpleDetailText(text = it)
            }
        }

        quote.holdingsTurnover?.let {
            QuoteDetailCell(
                label = stringResource(id = R.string.feature_quotes_holdings_turnover),
            ) {
                SimpleDetailText(text = it)
            }
        }

        quote.category?.let {
            QuoteDetailCell(
                label = stringResource(id = R.string.feature_quotes_fund_category),
            ) {
                SimpleDetailText(text = it)
            }
        }

        quote.morningstarRating?.let {
            QuoteDetailCell(
                label = stringResource(id = R.string.feature_quotes_morningstar_rating),
            ) {
                SimpleDetailText(text = it)
            }
        }

        quote.morningstarRisk?.let {
            QuoteDetailCell(
                label = stringResource(id = R.string.feature_quotes_morningstar_risk),
            ) {
                SimpleDetailText(text = it)
            }
        }

        quote.earningsDate?.let {
            QuoteDetailCell(
                label = stringResource(id = R.string.feature_quotes_earnings_date),
            ) {
                SimpleDetailText(text = it)
            }
        }

        quote.inceptionDate?.let {
            QuoteDetailCell(
                label = stringResource(id = R.string.feature_quotes_inception_date),
            ) {
                SimpleDetailText(text = it)
            }
        }
    }
}

@Composable
private fun Profile(quote: FullQuoteData) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        quote.about?.let {
            val isExpanded = remember { mutableStateOf(false) }
            val showMore = remember { mutableStateOf(false) }
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    letterSpacing = 1.25.sp,
                    maxLines = if (isExpanded.value) Int.MAX_VALUE else 10,
                    overflow = TextOverflow.Ellipsis,
                    onTextLayout = { textLayoutResult ->
                        if (textLayoutResult.hasVisualOverflow) {
                            showMore.value = true
                        }
                    }
                )
                if (showMore.value) {
                    ClickableText(
                        text = if (isExpanded.value) AnnotatedString(stringResource(id = R.string.feature_quotes_show_less))
                        else AnnotatedString(stringResource(id = R.string.feature_quotes_show_more)),
                        modifier = Modifier.align(Alignment.End),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            letterSpacing = 1.25.sp,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.End
                        ),
                        onClick = { isExpanded.value = !isExpanded.value },
                    )
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            quote.sector?.let {
                OutlinedButton(
                    onClick = { TODO("Navigate to sector") },
                    shape = CircleShape
                ) {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            quote.industry?.let {
                OutlinedButton(
                    onClick = { TODO("Navigate to industry") },
                    shape = CircleShape,
                ) {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun QuoteDetailCell(
    label: String,
    detailValue: @Composable () -> Unit,
) {
    VxmListItem(
        headlineContent = {
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall,
                letterSpacing = 1.25.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        },
        trailingContent = detailValue,
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    )
}

@Composable
private fun SimpleDetailText(
    text: String
) {
    Text(
        text = formatText(text),
        style = MaterialTheme.typography.labelLarge,
        letterSpacing = 1.5.sp,
        modifier = Modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(4.dp)
    )
}

@Composable
private fun PriceRangeLine(low: Double, high: Double, current: Double) {
    val fraction = ((current - low) / (high - low)).toFloat().coerceAtLeast(.01f)
    if (fraction.isNaN()) {
        return
    }
    Row(
        modifier = Modifier
            .fillMaxWidth(.65f),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SimpleDetailText(text = String.format(Locale.US, "%.2f", low))
        Box(
            modifier = Modifier
                .weight(fraction)
                .height(1.dp)
                .background(MaterialTheme.colorScheme.onSurface),
        )
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.onSurface),
        )
        Box(
            modifier = Modifier
                .weight((1 - fraction).coerceAtLeast(.01f))
                .height(1.dp)
                .background(MaterialTheme.colorScheme.onSurface),
        )
        SimpleDetailText(text = String.format(Locale.US, "%.2f", high))
    }
}

private fun formatText(text: String): String {
    // For Dividend Yield
    if (text.contains('(') && text.contains(')')) {
        val parts = text.split(' ')
        val firstPart = parts[0]
        val secondPart = parts[1].removeSurrounding("(", "%)")

        val formattedFirstPart = try {
            String.format(Locale.US, "%.2f", firstPart.toDouble())
        } catch (e: NumberFormatException) {
            firstPart
        }

        val formattedSecondPart = try {
            String.format(Locale.US, "%.2f", secondPart.toDouble())
        } catch (e: NumberFormatException) {
            secondPart
        }

        return "$formattedFirstPart ($formattedSecondPart%)"
    }

    return try {
        String.format(Locale.US, "%.2f", text.toDouble())
    } catch (e: NumberFormatException) {
        text
    }
}

@ThemePreviews
@Composable
private fun PreviewQuoteSummary() {
    VxmTheme {
        QuoteSummary(quote = previewFullQuoteData)
    }
}