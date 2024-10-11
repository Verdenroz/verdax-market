package com.verdenroz.verdaxmarket.feature.quotes.components.analysis

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.verdenroz.verdaxmarket.core.common.enums.Interval
import com.verdenroz.verdaxmarket.core.common.error.DataError
import com.verdenroz.verdaxmarket.core.common.result.Result
import com.verdenroz.verdaxmarket.core.designsystem.components.VxmListItem
import com.verdenroz.verdaxmarket.core.designsystem.theme.negativeTextColor
import com.verdenroz.verdaxmarket.core.designsystem.theme.positiveTextColor
import com.verdenroz.verdaxmarket.core.designsystem.util.UiText
import com.verdenroz.verdaxmarket.core.designsystem.util.asUiText
import com.verdenroz.verdaxmarket.core.model.AnalysisSignal
import com.verdenroz.verdaxmarket.core.model.AnalysisSignalSummary
import com.verdenroz.verdaxmarket.core.model.QuoteSignal
import com.verdenroz.verdaxmarket.core.model.indicators.Adx
import com.verdenroz.verdaxmarket.core.model.indicators.AnalysisIndicator
import com.verdenroz.verdaxmarket.core.model.indicators.Aroon
import com.verdenroz.verdaxmarket.core.model.indicators.BBands
import com.verdenroz.verdaxmarket.core.model.indicators.Cci
import com.verdenroz.verdaxmarket.core.model.indicators.IchimokuCloud
import com.verdenroz.verdaxmarket.core.model.indicators.IndicatorType
import com.verdenroz.verdaxmarket.core.model.indicators.Macd
import com.verdenroz.verdaxmarket.core.model.indicators.MovingAverage
import com.verdenroz.verdaxmarket.core.model.indicators.Rsi
import com.verdenroz.verdaxmarket.core.model.indicators.Srsi
import com.verdenroz.verdaxmarket.core.model.indicators.Stoch
import com.verdenroz.verdaxmarket.core.model.indicators.SuperTrend
import com.verdenroz.verdaxmarket.core.model.indicators.TechnicalIndicator
import com.verdenroz.verdaxmarket.feature.quotes.R

@Composable
internal fun QuoteAnalysis(
    snackbarHostState: SnackbarHostState,
    interval: Interval,
    signals: Map<Interval, Result<Map<TechnicalIndicator, AnalysisSignal>, DataError.Network>>,
    signalSummary: Map<Interval, Result<Map<IndicatorType, AnalysisSignalSummary>, DataError.Network>>,
    updateInterval: (Interval) -> Unit
) {
    val context = LocalContext.current
    when (val analysisSignals = signals[interval]) {
        is Result.Loading -> {
            StockAnalysisSkeleton()
        }

        is Result.Error, null -> {
            Column(
                modifier = Modifier
                    .height(300.dp)
                    .background(MaterialTheme.colorScheme.surfaceContainer),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AnalysisIntervalBar(
                    modifier = Modifier.fillMaxWidth(),
                    selectedInterval = interval,
                    updateInterval = updateInterval,
                )
                Text(
                    text = stringResource(id = R.string.feature_quotes_no_analysis),
                    style = MaterialTheme.typography.titleSmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 64.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(100))
                        .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                )
            }

            if (analysisSignals is Result.Error) {
                LaunchedEffect(analysisSignals.error) {
                    snackbarHostState.showSnackbar(
                        message = analysisSignals.error.asUiText().asString(context),
                        actionLabel = UiText.StringResource(R.string.feature_quotes_dismiss)
                            .asString(context),
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }

        is Result.Success -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {

                AnalysisIntervalBar(
                    modifier = Modifier.fillMaxWidth(),
                    selectedInterval = interval,
                    updateInterval = updateInterval,
                )

                when (val analysisSignalSummary = signalSummary[interval]) {
                    is Result.Success -> {
                        SummaryAnalysisSection(analysisSignalSummary = analysisSignalSummary.data)
                    }

                    else -> {
                        // Nothing
                    }
                }

                if (analysisSignals.data.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(id = R.string.feature_quotes_no_analysis),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                } else {
                    AnalysisSection(
                        signals = analysisSignals.data.filterKeys { it in TechnicalIndicator.MOVING_AVERAGES }
                    )
                    AnalysisSection(
                        signals = analysisSignals.data.filterKeys { it in TechnicalIndicator.OSCILLATORS }
                    )
                    AnalysisSection(
                        signals = analysisSignals.data.filterKeys { it in TechnicalIndicator.TRENDS }
                    )
                }
            }
        }
    }
}

@Composable
private fun AnalysisIntervalBar(
    modifier: Modifier = Modifier,
    selectedInterval: Interval,
    updateInterval: (Interval) -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Interval.entries.filter { interval ->
            interval in setOf(
                Interval.FIFTEEN_MINUTE,
                Interval.THIRTY_MINUTE,
                Interval.ONE_HOUR,
                Interval.DAILY,
                Interval.WEEKLY,
                Interval.MONTHLY
            )
        }.forEach { interval ->
            IntervalButton(
                interval = interval,
                selectedInterval = interval == selectedInterval,
                onClick = { updateInterval(interval) }
            )
        }
    }
}

@Composable
private fun IntervalButton(
    interval: Interval,
    selectedInterval: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        RadioButton(
            selected = selectedInterval,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = MaterialTheme.colorScheme.secondary,
                unselectedColor = MaterialTheme.colorScheme.onSurface
            )
        )
        Text(
            text = interval.value.uppercase(),
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
private fun AnalysisSection(
    signals: Map<TechnicalIndicator, AnalysisSignal>
) {
    Column {
        signals.forEach { (indicator, analysis) ->
            AnalysisDetail(
                analysis = analysis,
                indicator = indicator
            )
        }
    }
}

@Composable
private fun AnalysisDetail(
    analysis: AnalysisSignal,
    indicator: TechnicalIndicator
) {
    val signal = analysis.signal
    val displayValue = analysis.indicator

    VxmListItem(
        headlineContent = {
            Text(
                text = indicator.asString(),
                style = MaterialTheme.typography.titleSmall,
                letterSpacing = 1.25.sp,
            )
        },
        trailingContent = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth(.35f)
            ) {
                Text(
                    text = displayValue.asString(),
                    style = MaterialTheme.typography.labelLarge,
                    letterSpacing = 1.25.sp
                )
                Text(
                    text = signal.asString(),
                    style = MaterialTheme.typography.titleSmall,
                    letterSpacing = if (signal.name.length <= 4) 1.25.sp else 1.sp,
                    color = when (signal) {
                        QuoteSignal.BUY -> positiveTextColor
                        QuoteSignal.NEUTRAL -> MaterialTheme.colorScheme.onSurface
                        QuoteSignal.SELL -> negativeTextColor
                    },
                    fontSize = 14.sp
                )
            }
        }
    )
    HorizontalDivider(
        color = MaterialTheme.colorScheme.inverseOnSurface,
        thickness = Dp.Hairline
    )
}

@Composable
private fun AnalysisIndicator.asString(): String {
    return when (this) {
        is MovingAverage -> this.value.toString()
        is Cci -> this.cci.toString()
        is Rsi -> this.rsi.toString()
        is Srsi -> this.srsi.toString()
        is Stoch -> this.stoch.toString()
        is Adx -> this.adx.toString()
        is Aroon -> this.aroonUp.toString()
        is BBands -> this.upperBand.toString()
        is IchimokuCloud -> this.leadingSpanA.toString()
        is Macd -> this.macd.toString()
        is SuperTrend -> this.superTrend.toString()
    }
}

@Composable
private fun TechnicalIndicator.asString(): String {
    return when (this) {
        TechnicalIndicator.SMA10 -> stringResource(id = R.string.feature_quotes_sma10)
        TechnicalIndicator.SMA20 -> stringResource(id = R.string.feature_quotes_sma20)
        TechnicalIndicator.SMA50 -> stringResource(id = R.string.feature_quotes_sma50)
        TechnicalIndicator.SMA100 -> stringResource(id = R.string.feature_quotes_sma100)
        TechnicalIndicator.SMA200 -> stringResource(id = R.string.feature_quotes_sma200)
        TechnicalIndicator.EMA10 -> stringResource(id = R.string.feature_quotes_ema10)
        TechnicalIndicator.EMA20 -> stringResource(id = R.string.feature_quotes_ema20)
        TechnicalIndicator.EMA50 -> stringResource(id = R.string.feature_quotes_ema50)
        TechnicalIndicator.EMA100 -> stringResource(id = R.string.feature_quotes_ema100)
        TechnicalIndicator.EMA200 -> stringResource(id = R.string.feature_quotes_ema200)
        TechnicalIndicator.WMA10 -> stringResource(id = R.string.feature_quotes_wma10)
        TechnicalIndicator.WMA20 -> stringResource(id = R.string.feature_quotes_wma20)
        TechnicalIndicator.WMA50 -> stringResource(id = R.string.feature_quotes_wma50)
        TechnicalIndicator.WMA100 -> stringResource(id = R.string.feature_quotes_wma100)
        TechnicalIndicator.WMA200 -> stringResource(id = R.string.feature_quotes_wma200)
        TechnicalIndicator.VWMA20 -> stringResource(id = R.string.feature_quotes_vwma20)
        TechnicalIndicator.RSI -> stringResource(id = R.string.feature_quotes_rsi14)
        TechnicalIndicator.SRSI -> stringResource(id = R.string.feature_quotes_srsi14)
        TechnicalIndicator.CCI -> stringResource(id = R.string.feature_quotes_cci20)
        TechnicalIndicator.ADX -> stringResource(id = R.string.feature_quotes_adx14)
        TechnicalIndicator.MACD -> stringResource(id = R.string.feature_quotes_macd)
        TechnicalIndicator.STOCH -> stringResource(id = R.string.feature_quotes_stoch)
        TechnicalIndicator.AROON -> stringResource(id = R.string.feature_quotes_aroon)
        TechnicalIndicator.BBANDS -> stringResource(id = R.string.feature_quotes_bbands)
        TechnicalIndicator.SUPERTREND -> stringResource(id = R.string.feature_quotes_super_trend)
        TechnicalIndicator.ICHIMOKUCLOUD -> stringResource(id = R.string.feature_quotes_ichimoku)
    }
}

@Composable
private fun QuoteSignal.asString(): String {
    return when (this) {
        QuoteSignal.BUY -> stringResource(id = R.string.feature_quotes_buy)
        QuoteSignal.SELL -> stringResource(id = R.string.feature_quotes_sell)
        QuoteSignal.NEUTRAL -> stringResource(id = R.string.feature_quotes_neutral)
    }
}

@Composable
private fun StockAnalysisSkeleton(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.surfaceContainer
) {
    Box(
        modifier = modifier
            .height(300.dp)
            .fillMaxWidth()
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.secondary
        )
    }
}