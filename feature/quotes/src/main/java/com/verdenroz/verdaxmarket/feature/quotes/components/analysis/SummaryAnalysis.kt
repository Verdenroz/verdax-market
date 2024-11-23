package com.verdenroz.verdaxmarket.feature.quotes.components.analysis

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.verdenroz.verdaxmarket.core.designsystem.theme.getNegativeBackgroundColor
import com.verdenroz.verdaxmarket.core.designsystem.theme.getNegativeTextColor
import com.verdenroz.verdaxmarket.core.designsystem.theme.getPositiveBackgroundColor
import com.verdenroz.verdaxmarket.core.designsystem.theme.getPositiveTextColor
import com.verdenroz.verdaxmarket.core.model.AnalysisSignalSummary
import com.verdenroz.verdaxmarket.core.model.indicators.IndicatorType
import com.verdenroz.verdaxmarket.feature.quotes.R

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun SummaryAnalysisSection(
    analysisSignalSummary: Map<IndicatorType, AnalysisSignalSummary>,
) {
    val overallSummary = analysisSignalSummary[IndicatorType.ALL]
    val overallActions = Triple(
        overallSummary?.buy ?: 0,
        overallSummary?.neutral ?: 0,
        overallSummary?.sell ?: 0
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OverallSummaryCircle(
            title = IndicatorType.ALL.asString(),
            actions = overallActions,
            modifier = Modifier.size(150.dp)
        )
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalArrangement = Arrangement.SpaceAround,
            maxItemsInEachRow = 2
        ) {
            analysisSignalSummary.filter { signal -> signal.key != IndicatorType.ALL }
                .forEach { (indicatorType, summary) ->
                    val (buy, neutral, sell) = Triple(
                        summary.buy,
                        summary.neutral,
                        summary.sell
                    )
                    SummaryCircle(
                        title = indicatorType.asString(),
                        actions = Triple(buy, neutral, sell),
                        modifier = Modifier.size(100.dp)
                    )
                }
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

@Composable
private fun OverallSummaryCircle(
    title: String,
    actions: Triple<Int, Int, Int>,
    modifier: Modifier = Modifier
) {
    val (buy, neutral, sell) = actions
    val total = buy + neutral + sell
    val buyWeight = buy.toFloat() / total
    val neutralWeight = neutral.toFloat() / total
    val sellWeight = sell.toFloat() / total
    val positiveBackground = getPositiveBackgroundColor()
    val negativeBackgroundColor = getNegativeBackgroundColor()

    val suggestion = when {
        buy > 2 * (sell + neutral) -> stringResource(id = R.string.feature_quotes_strong_buy)
        buy > sell && buy > neutral -> stringResource(id = R.string.feature_quotes_buy)
        sell > buy && sell > neutral -> stringResource(id = R.string.feature_quotes_sell)
        sell > 2 * (buy + neutral) -> stringResource(id = R.string.feature_quotes_strong_sell)
        else -> stringResource(id = R.string.feature_quotes_neutral)
    }

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            letterSpacing = 1.25.sp,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onSurface
        )
        Canvas(
            modifier = modifier
        ) {
            val sweepAngleBuy = 360 * buyWeight
            val sweepAngleNeutral = 360 * neutralWeight
            val sweepAngleSell = 360 * sellWeight

            drawArc(
                color = positiveBackground,
                startAngle = 0f,
                sweepAngle = sweepAngleBuy,
                useCenter = true
            )
            drawArc(
                color = Color.Gray,
                startAngle = sweepAngleBuy,
                sweepAngle = sweepAngleNeutral,
                useCenter = true
            )
            drawArc(
                color = negativeBackgroundColor,
                startAngle = sweepAngleBuy + sweepAngleNeutral,
                sweepAngle = sweepAngleSell,
                useCenter = true
            )
        }
        Text(
            text = suggestion,
            style = MaterialTheme.typography.titleLarge,
            color = when (suggestion) {
                stringResource(id = R.string.feature_quotes_buy), stringResource(id = R.string.feature_quotes_strong_buy) -> getPositiveTextColor()
                stringResource(id = R.string.feature_quotes_sell), stringResource(id = R.string.feature_quotes_strong_sell) -> getNegativeTextColor()
                else -> MaterialTheme.colorScheme.onSurface
            },
            letterSpacing = 1.25.sp,
            fontWeight = FontWeight.ExtraBold,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FlowRowScope.SummaryCircle(
    title: String,
    actions: Triple<Int, Int, Int>,
    modifier: Modifier = Modifier
) {
    val (buy, neutral, sell) = actions
    val total = buy + neutral + sell
    val buyWeight = buy.toFloat() / total
    val neutralWeight = neutral.toFloat() / total
    val sellWeight = sell.toFloat() / total
    val positiveBackgroundColor = getPositiveBackgroundColor()
    val negativeBackgroundColor = getNegativeBackgroundColor()

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
            .weight(.25f)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            letterSpacing = 1.25.sp,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onSurface
        )
        Canvas(
            modifier = modifier
        ) {
            val sweepAngleBuy = 360 * buyWeight
            val sweepAngleNeutral = 360 * neutralWeight
            val sweepAngleSell = 360 * sellWeight

            drawArc(
                color = positiveBackgroundColor,
                startAngle = 0f,
                sweepAngle = sweepAngleBuy,
                useCenter = true
            )
            drawArc(
                color = Color.Gray,
                startAngle = sweepAngleBuy,
                sweepAngle = sweepAngleNeutral,
                useCenter = true
            )
            drawArc(
                color = negativeBackgroundColor,
                startAngle = sweepAngleBuy + sweepAngleNeutral,
                sweepAngle = sweepAngleSell,
                useCenter = true
            )
        }
        Text(
            text = suggestion,
            style = MaterialTheme.typography.titleMedium,
            color = when (suggestion) {
                stringResource(id = R.string.feature_quotes_buy), stringResource(id = R.string.feature_quotes_strong_buy) -> getPositiveTextColor()
                stringResource(id = R.string.feature_quotes_sell), stringResource(id = R.string.feature_quotes_strong_sell) -> getNegativeTextColor()
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
                title = stringResource(id = R.string.feature_quotes_buy),
                value = buy
            )
            SummaryCountCell(
                title = stringResource(id = R.string.feature_quotes_neutral),
                value = neutral
            )
            SummaryCountCell(
                title = stringResource(id = R.string.feature_quotes_sell),
                value = sell
            )
        }
    }
}

@Composable
private fun SummaryCountCell(
    title: String,
    value: Int
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}