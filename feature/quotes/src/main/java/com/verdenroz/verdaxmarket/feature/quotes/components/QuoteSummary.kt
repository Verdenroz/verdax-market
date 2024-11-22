package com.verdenroz.verdaxmarket.feature.quotes.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.verdenroz.verdaxmarket.core.designsystem.components.VxmListItem
import com.verdenroz.verdaxmarket.core.designsystem.icons.VxmIcons
import com.verdenroz.verdaxmarket.core.designsystem.theme.ThemePreviews
import com.verdenroz.verdaxmarket.core.designsystem.theme.VxmTheme
import com.verdenroz.verdaxmarket.core.model.FullQuoteData
import com.verdenroz.verdaxmarket.feature.quotes.R
import com.verdenroz.verdaxmarket.feature.quotes.previewFullQuoteData
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
internal fun QuoteSummary(
    quote: FullQuoteData,
    isHintsEnabled: Boolean,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.surfaceContainerLow),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Profile(quote = quote)
        quote.open?.let {
            QuoteDetailCell(
                label = stringResource(id = R.string.feature_quotes_open),
                isHintsEnabled = isHintsEnabled,
                hint = stringResource(id = R.string.feature_quotes_open_hint)
            ) {
                SimpleDetailText(
                    text = quote.open.toString()
                )
            }
        }
        if (quote.low != null && quote.high != null)
            QuoteDetailCell(
                label = stringResource(id = R.string.feature_quotes_days_range),
                isHintsEnabled = isHintsEnabled,
                hint = stringResource(id = R.string.feature_quotes_days_range_hint)
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
                isHintsEnabled = isHintsEnabled,
                hint = stringResource(id = R.string.feature_quotes_fifty_two_week_range_hint)
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
                isHintsEnabled = isHintsEnabled,
                hint = stringResource(id = R.string.feature_quotes_volume_hint)
            ) {
                SimpleDetailText(text = formatVolume(quote.volume!!))
            }
        if (quote.avgVolume != null)
            QuoteDetailCell(
                label = stringResource(id = R.string.feature_quotes_avg_volume),
                isHintsEnabled = isHintsEnabled,
                hint = stringResource(id = R.string.feature_quotes_avg_volume_hint)
            ) {
                SimpleDetailText(text =formatVolume(quote.avgVolume!!))
            }

        quote.marketCap?.let {
            QuoteDetailCell(
                label = stringResource(id = R.string.feature_quotes_market_cap),
                isHintsEnabled = isHintsEnabled,
                hint = stringResource(id = R.string.feature_quotes_market_cap_hint)
            ) {
                SimpleDetailText(text = it)
            }
        }
        quote.netAssets?.let {
            QuoteDetailCell(
                label = stringResource(id = R.string.feature_quotes_net_assets),
                isHintsEnabled = isHintsEnabled,
                hint = stringResource(id = R.string.feature_quotes_net_assets_hint)
            ) {
                SimpleDetailText(text = it)
            }
        }
        quote.nav?.let {
            QuoteDetailCell(
                label = stringResource(id = R.string.feature_quotes_nav),
                isHintsEnabled = isHintsEnabled,
                hint = stringResource(id = R.string.feature_quotes_nav_hint)
            ) {
                SimpleDetailText(text = it.toString())
            }
        }
        quote.pe?.let {
            QuoteDetailCell(
                label = stringResource(id = R.string.feature_quotes_pe_ratio),
                isHintsEnabled = isHintsEnabled,
                hint = stringResource(id = R.string.feature_quotes_pe_ratio_hint)
            ) {
                SimpleDetailText(text = it)
            }
        }
        quote.eps?.let {
            QuoteDetailCell(
                label = stringResource(id = R.string.feature_quotes_eps),
                isHintsEnabled = isHintsEnabled,
                hint = stringResource(id = R.string.feature_quotes_eps_hint)
            ) {
                SimpleDetailText(text = it)
            }
        }
        quote.beta?.let {
            QuoteDetailCell(
                label = stringResource(id = R.string.feature_quotes_beta),
                isHintsEnabled = isHintsEnabled,
                hint = stringResource(id = R.string.feature_quotes_beta_hint)
            ) {
                SimpleDetailText(text = it)
            }
        }

        quote.expenseRatio?.let {
            QuoteDetailCell(
                label = stringResource(id = R.string.feature_quotes_expense_ratio),
                isHintsEnabled = isHintsEnabled,
                hint = stringResource(id = R.string.feature_quotes_expense_ratio_hint)
            ) {
                SimpleDetailText(text = it)
            }
        }

        quote.dividend?.let {
            QuoteDetailCell(
                label = stringResource(id = R.string.feature_quotes_dividend_yield),
                isHintsEnabled = isHintsEnabled,
                hint = stringResource(id = R.string.feature_quotes_dividend_yield_hint)
            ) {
                SimpleDetailText(text = it + " (" + quote.yield + ")")
            }
        }

        quote.lastDividend?.let {
            QuoteDetailCell(
                label = stringResource(id = R.string.feature_quotes_last_dividend),
                isHintsEnabled = isHintsEnabled,
                hint = stringResource(id = R.string.feature_quotes_last_dividend_hint)
            ) {
                SimpleDetailText(text = it)
            }
        }

        quote.exDividend?.let {
            QuoteDetailCell(
                label = stringResource(id = R.string.feature_quotes_ex_dividend),
                isHintsEnabled = isHintsEnabled,
                hint = stringResource(id = R.string.feature_quotes_ex_dividend_hint)
            ) {
                SimpleDetailText(text = it)
            }
        }

        quote.lastCapitalGain?.let {
            QuoteDetailCell(
                label = stringResource(id = R.string.feature_quotes_last_capital_gain),
                isHintsEnabled = isHintsEnabled,
                hint = stringResource(id = R.string.feature_quotes_last_capital_gain_hint)
            ) {
                SimpleDetailText(text = it)
            }
        }

        quote.holdingsTurnover?.let {
            QuoteDetailCell(
                label = stringResource(id = R.string.feature_quotes_holdings_turnover),
                isHintsEnabled = isHintsEnabled,
                hint = stringResource(id = R.string.feature_quotes_holdings_turnover_hint)
            ) {
                SimpleDetailText(text = it)
            }
        }

        quote.category?.let {
            QuoteDetailCell(
                label = stringResource(id = R.string.feature_quotes_fund_category),
                isHintsEnabled = isHintsEnabled,
                hint = stringResource(id = R.string.feature_quotes_fund_category_hint)
            ) {
                SimpleDetailText(text = it)
            }
        }

        quote.morningstarRating?.let {
            QuoteDetailCell(
                label = stringResource(id = R.string.feature_quotes_morningstar_rating),
                isHintsEnabled = isHintsEnabled,
                hint = stringResource(id = R.string.feature_quotes_morningstar_rating_hint)
            ) {
                SimpleDetailText(text = it)
            }
        }

        quote.morningstarRisk?.let {
            QuoteDetailCell(
                label = stringResource(id = R.string.feature_quotes_morningstar_risk),
                isHintsEnabled = isHintsEnabled,
                hint = stringResource(id = R.string.feature_quotes_morningstar_risk_hint)
            ) {
                SimpleDetailText(text = it)
            }
        }

        quote.earningsDate?.let {
            QuoteDetailCell(
                label = stringResource(id = R.string.feature_quotes_earnings_date),
                isHintsEnabled = isHintsEnabled,
                hint = stringResource(id = R.string.feature_quotes_earnings_date_hint)
            ) {
                SimpleDetailText(text = it)
            }
        }

        quote.inceptionDate?.let {
            QuoteDetailCell(
                label = stringResource(id = R.string.feature_quotes_inception_date),
                isHintsEnabled = isHintsEnabled,
                hint = stringResource(id = R.string.feature_quotes_inception_date_hint)
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
            Column {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    letterSpacing = 1.25.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = if (isExpanded.value) Int.MAX_VALUE else 10,
                    overflow = TextOverflow.Ellipsis,
                    onTextLayout = { textLayoutResult ->
                        if (textLayoutResult.hasVisualOverflow) {
                            showMore.value = true
                        }
                    }
                )
                if (showMore.value) {
                    TextButton(
                        onClick = { isExpanded.value = !isExpanded.value },
                        modifier = Modifier.align(Alignment.End),
                    ) {
                        Text(
                            text = if (isExpanded.value) AnnotatedString(stringResource(id = R.string.feature_quotes_show_less))
                            else AnnotatedString(stringResource(id = R.string.feature_quotes_show_more)),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                letterSpacing = 1.25.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        )
                    }

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuoteDetailCell(
    label: String,
    isHintsEnabled: Boolean = false,
    hint: String = "",
    detailValue: @Composable () -> Unit,
) {
    VxmListItem(
        headlineContent = {
            var tooltipVisible by remember { mutableStateOf(false) }
            val tooltipState = rememberTooltipState(initialIsVisible = tooltipVisible)
            val scope = rememberCoroutineScope()
            TooltipBox(
                positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                tooltip = {
                    PlainTooltip(
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                        modifier = Modifier.border(
                            1.dp,
                            MaterialTheme.colorScheme.secondary,
                            CircleShape
                        )
                    ) {
                        Text(
                            text = hint,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                },
                state = tooltipState
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.titleSmall,
                        letterSpacing = 1.25.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.pointerInput(Unit) {
                            detectTapGestures(
                                onLongPress = {
                                    if (isHintsEnabled) scope.launch { tooltipState.show() }
                                },
                                onTap = {
                                    if (isHintsEnabled) scope.launch { tooltipState.show() }
                                }
                            )
                        }
                    )
                    if (isHintsEnabled && hint.isNotEmpty()) {
                        Box {
                            IconButton(
                                onClick = { scope.launch { tooltipState.show() } },
                                modifier = Modifier.size(16.dp)
                            ) {
                                Icon(
                                    imageVector = VxmIcons.Help,
                                    contentDescription = stringResource(
                                        R.string.feature_quotes_hint_icon_description,
                                        label
                                    ),
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        },
        trailingContent = detailValue,
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    )
}

@Composable
private fun SimpleDetailText(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = formatText(text),
        style = MaterialTheme.typography.labelLarge,
        letterSpacing = 1.5.sp,
        modifier = modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(4.dp)
    )
}

@Composable
private fun PriceRangeLine(low: String, high: String, current: String) {
    val lowValue = low.replace(",", "").toDoubleOrNull() ?: return
    val highValue = high.replace(",", "").toDoubleOrNull() ?: return
    val currentValue = current.replace(",", "").toDoubleOrNull() ?: return

    val fraction = ((currentValue - lowValue) / (highValue - lowValue)).toFloat().coerceAtLeast(.01f)
    if (fraction.isNaN()) {
        return
    }

    Row(
        modifier = Modifier
            .fillMaxWidth(.5f),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SimpleDetailText(text = String.format(Locale.US, "%.2f", lowValue))
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
        SimpleDetailText(text = String.format(Locale.US, "%.2f", highValue))
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
        } catch (_: NumberFormatException) {
            firstPart
        }

        val formattedSecondPart = try {
            String.format(Locale.US, "%.2f", secondPart.toDouble())
        } catch (_: NumberFormatException) {
            secondPart
        }

        return "$formattedFirstPart ($formattedSecondPart%)"
    }

    return try {
        String.format(Locale.US, "%.2f", text.toDouble())
    } catch (_: NumberFormatException) {
        text
    }
}

private fun formatVolume(volume: Long): String {
    return when {
        volume >= 1_000_000_000 -> String.format(Locale.US, "%.2fB", volume / 1_000_000_000.0)
        volume >= 1_000_000 -> String.format(Locale.US, "%.2fM", volume / 1_000_000.0)
        volume >= 1_000 -> String.format(Locale.US, "%.2fK", volume / 1_000.0)
        else -> volume.toString()
    }
}

@ThemePreviews
@Composable
private fun PreviewQuoteSummary() {
    VxmTheme {
        QuoteSummary(
            quote = previewFullQuoteData,
            isHintsEnabled = true
        )
    }
}