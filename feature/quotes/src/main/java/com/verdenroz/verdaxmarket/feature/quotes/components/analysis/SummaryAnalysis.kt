package com.verdenroz.verdaxmarket.feature.quotes.components.analysis

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.verdenroz.verdaxmarket.core.designsystem.theme.negativeBackgroundColor
import com.verdenroz.verdaxmarket.core.designsystem.theme.negativeTextColor
import com.verdenroz.verdaxmarket.core.designsystem.theme.positiveBackgroundColor
import com.verdenroz.verdaxmarket.core.designsystem.theme.positiveTextColor
import com.verdenroz.verdaxmarket.core.model.AnalysisSignalSummary
import com.verdenroz.verdaxmarket.core.model.indicators.IndicatorType
import com.verdenroz.verdaxmarket.feature.quotes.R

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun SummaryAnalysisSection(
    analysisSignalSummary: Map<IndicatorType, AnalysisSignalSummary>,
) {
    FlowRow(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center
    ) {
        analysisSignalSummary.forEach { (indicatorType, summary) ->
            val (sell, neutral, buy) = Triple(
                summary.buy,
                summary.neutral,
                summary.sell
            )
            SummaryBar(
                title = indicatorType.asString(),
                actions = Triple(sell, neutral, buy),
                summary = summary.summary,
            )
        }
    }
}

@Composable
private fun IndicatorType.asString(): String {
    return when (this) {
        IndicatorType.MOVING_AVERAGE -> stringResource(id = R.string.feature_quotes_moving_averages)
        IndicatorType.OSCILLATOR -> stringResource(id = R.string.feature_quotes_oscillators)
        IndicatorType.TREND -> stringResource(id = R.string.feature_quotes_trends)
        IndicatorType.ALL -> stringResource(id = R.string.feature_quotes_all)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FlowRowScope.SummaryBar(
    title: String,
    actions: Triple<Int, Int, Int>,
    summary: Double,
) {
    val epsilon = 0.001f // small constant to ensure weight is always > 0
    val (sell, neutral, buy) = actions
    val suggestion = when {
        // If buy is greater than sell and neutral combined by 2 times, then it is a strong buy
        buy > 2 * (sell + neutral) -> stringResource(id = R.string.feature_quotes_strong_buy)

        // If buy is greater than sell and neutral, then it is a buy
        buy > sell && buy > neutral -> stringResource(id = R.string.feature_quotes_buy)

        // If sell is greater than buy and neutral, then it is a sell
        sell > buy && sell > neutral -> stringResource(id = R.string.feature_quotes_sell)

        // If sell is greater than buy and neutral combined by 2 times, then it is a strong sell
        sell > 2 * (buy + neutral) -> stringResource(id = R.string.feature_quotes_strong_sell)

        else -> stringResource(id = R.string.feature_quotes_neutral)
    }
    Column(
        modifier = Modifier
            .padding(16.dp)
            .weight(.5f),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            letterSpacing = 1.25.sp,
            fontWeight = FontWeight.Black
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .weight(((1 - summary) / 2).toFloat() + epsilon)
                    .fillMaxHeight()
                    .background(negativeBackgroundColor)
            )
            Box(
                modifier = Modifier
                    .weight(((1 + summary) / 2).toFloat() + epsilon)
                    .fillMaxHeight()
                    .background(positiveBackgroundColor)
            )
        }
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = suggestion,
                style = MaterialTheme.typography.titleMedium,
                color = when (suggestion) {
                    stringResource(id = R.string.feature_quotes_buy), stringResource(id = R.string.feature_quotes_strong_buy) -> positiveTextColor
                    stringResource(id = R.string.feature_quotes_sell), stringResource(id = R.string.feature_quotes_strong_sell) -> negativeTextColor
                    else -> MaterialTheme.colorScheme.onSurface
                },
                letterSpacing = 1.25.sp,
                fontWeight = FontWeight.ExtraBold,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SummaryCountCell(
                    title = stringResource(id = R.string.feature_quotes_sell),
                    value = sell
                )
                SummaryCountCell(
                    title = stringResource(id = R.string.feature_quotes_neutral),
                    value = neutral
                )
                SummaryCountCell(
                    title = stringResource(id = R.string.feature_quotes_buy),
                    value = buy
                )
            }
        }
    }
}

@Composable
private fun SummaryCountCell(
    title: String,
    value: Int
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge
        )
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.labelLarge
        )
    }
}