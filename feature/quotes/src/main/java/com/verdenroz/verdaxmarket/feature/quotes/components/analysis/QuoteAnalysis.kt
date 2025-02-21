package com.verdenroz.verdaxmarket.feature.quotes.components.analysis

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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.verdenroz.verdaxmarket.core.common.enums.Interval
import com.verdenroz.verdaxmarket.core.common.error.DataError
import com.verdenroz.verdaxmarket.core.common.result.Result
import com.verdenroz.verdaxmarket.core.designsystem.components.VxmListItem
import com.verdenroz.verdaxmarket.core.designsystem.icons.VxmIcons
import com.verdenroz.verdaxmarket.core.designsystem.theme.ThemePreviews
import com.verdenroz.verdaxmarket.core.designsystem.theme.VxmTheme
import com.verdenroz.verdaxmarket.core.designsystem.theme.getNegativeTextColor
import com.verdenroz.verdaxmarket.core.designsystem.theme.getPositiveTextColor
import com.verdenroz.verdaxmarket.core.model.AnalysisSignal
import com.verdenroz.verdaxmarket.core.model.AnalysisSignalSummary
import com.verdenroz.verdaxmarket.core.model.enums.QuoteSignal
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
import kotlinx.coroutines.launch

@Composable
internal fun QuoteAnalysis(
    isHintsEnabled: Boolean,
    interval: Interval,
    signals: Map<Interval, Result<Map<TechnicalIndicator, AnalysisSignal>, DataError.Network>>,
    signalSummary: Map<Interval, Result<Map<IndicatorType, AnalysisSignalSummary>, DataError.Network>>,
    updateInterval: (Interval) -> Unit,
) {
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
                        signals = analysisSignals.data.filterKeys { it in TechnicalIndicator.MOVING_AVERAGES },
                        isHintsEnabled = isHintsEnabled
                    )
                    AnalysisSection(
                        signals = analysisSignals.data.filterKeys { it in TechnicalIndicator.OSCILLATORS },
                        isHintsEnabled = isHintsEnabled
                    )
                    AnalysisSection(
                        signals = analysisSignals.data.filterKeys { it in TechnicalIndicator.TRENDS },
                        isHintsEnabled = isHintsEnabled
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
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun AnalysisSection(
    signals: Map<TechnicalIndicator, AnalysisSignal>,
    isHintsEnabled: Boolean
) {
    Column {
        signals.forEach { (indicator, analysis) ->
            AnalysisDetail(
                analysis = analysis,
                indicator = indicator,
                isHintsEnabled = isHintsEnabled
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AnalysisDetail(
    analysis: AnalysisSignal,
    indicator: TechnicalIndicator,
    isHintsEnabled: Boolean
) {
    val signal = analysis.signal
    val displayValue = analysis.indicator

    if (displayValue.asString() == "null") {
        return
    }

    VxmListItem(
        headlineContent = {
            val tooltipState = rememberTooltipState(initialIsVisible = false)
            val scope = rememberCoroutineScope()
            TooltipBox(
                positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                tooltip = {
                    PlainTooltip(
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceContainer)
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.secondary,
                                CircleShape
                            )
                            .widthIn(max = 280.dp)
                            .padding(8.dp)
                    ) {
                        Text(
                            text = indicator.hint(),
                            style = MaterialTheme.typography.bodySmall,
                            softWrap = true,
                            lineHeight = 16.sp
                        )
                    }
                },
                state = tooltipState
            ) {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = indicator.asString(),
                        style = MaterialTheme.typography.titleSmall,
                        letterSpacing = 1.25.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(1f)
                            .pointerInput(Unit) {
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
                    if (isHintsEnabled && indicator.hint().isNotEmpty()) {
                        IconButton(
                            onClick = { scope.launch { tooltipState.show() } },
                            modifier = Modifier
                                .padding(start = 4.dp)
                                .size(16.dp)
                        ) {
                            Icon(
                                imageVector = VxmIcons.Help,
                                contentDescription = stringResource(
                                    R.string.feature_quotes_hint_icon_description,
                                    indicator.hint()
                                ),
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        },
        trailingContent = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(.3f)
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
                        QuoteSignal.BUY -> getPositiveTextColor()
                        QuoteSignal.NEUTRAL -> MaterialTheme.colorScheme.onSurface
                        QuoteSignal.SELL -> getNegativeTextColor()
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
        is Srsi -> this.k.toString()
        is Stoch -> this.k.toString()
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
private fun TechnicalIndicator.hint(): String {
    return when (this) {
        TechnicalIndicator.SMA10 -> stringResource(id = R.string.feature_quotes_sma10_hint)
        TechnicalIndicator.SMA20 -> stringResource(id = R.string.feature_quotes_sma20_hint)
        TechnicalIndicator.SMA50 -> stringResource(id = R.string.feature_quotes_sma50_hint)
        TechnicalIndicator.SMA100 -> stringResource(id = R.string.feature_quotes_sma100_hint)
        TechnicalIndicator.SMA200 -> stringResource(id = R.string.feature_quotes_sma200_hint)
        TechnicalIndicator.EMA10 -> stringResource(id = R.string.feature_quotes_ema10_hint)
        TechnicalIndicator.EMA20 -> stringResource(id = R.string.feature_quotes_ema20_hint)
        TechnicalIndicator.EMA50 -> stringResource(id = R.string.feature_quotes_ema50_hint)
        TechnicalIndicator.EMA100 -> stringResource(id = R.string.feature_quotes_ema100_hint)
        TechnicalIndicator.EMA200 -> stringResource(id = R.string.feature_quotes_ema200_hint)
        TechnicalIndicator.WMA10 -> stringResource(id = R.string.feature_quotes_wma10_hint)
        TechnicalIndicator.WMA20 -> stringResource(id = R.string.feature_quotes_wma20_hint)
        TechnicalIndicator.WMA50 -> stringResource(id = R.string.feature_quotes_wma50_hint)
        TechnicalIndicator.WMA100 -> stringResource(id = R.string.feature_quotes_wma100_hint)
        TechnicalIndicator.WMA200 -> stringResource(id = R.string.feature_quotes_wma200_hint)
        TechnicalIndicator.VWMA20 -> stringResource(id = R.string.feature_quotes_vwma20_hint)
        TechnicalIndicator.RSI -> stringResource(id = R.string.feature_quotes_rsi14_hint)
        TechnicalIndicator.SRSI -> stringResource(id = R.string.feature_quotes_srsi14_hint)
        TechnicalIndicator.CCI -> stringResource(id = R.string.feature_quotes_cci20_hint)
        TechnicalIndicator.ADX -> stringResource(id = R.string.feature_quotes_adx14_hint)
        TechnicalIndicator.MACD -> stringResource(id = R.string.feature_quotes_macd_hint)
        TechnicalIndicator.STOCH -> stringResource(id = R.string.feature_quotes_stoch_hint)
        TechnicalIndicator.AROON -> stringResource(id = R.string.feature_quotes_aroon_hint)
        TechnicalIndicator.BBANDS -> stringResource(id = R.string.feature_quotes_bbands_hint)
        TechnicalIndicator.SUPERTREND -> stringResource(id = R.string.feature_quotes_super_trend_hint)
        TechnicalIndicator.ICHIMOKUCLOUD -> stringResource(id = R.string.feature_quotes_ichimoku_hint)
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

@ThemePreviews
@Composable
private fun PreviewQuoteAnalysis() {
    VxmTheme {
        QuoteAnalysis(
            isHintsEnabled = true,
            interval = Interval.DAILY,
            signals = mapOf(
                Interval.DAILY to Result.Success(
                    mapOf(
                        TechnicalIndicator.SMA50 to AnalysisSignal.MovingAverageSignal(
                            signal = QuoteSignal.NEUTRAL,
                            indicator = MovingAverage(50.0)
                        ),
                        TechnicalIndicator.EMA50 to AnalysisSignal.MovingAverageSignal(
                            signal = QuoteSignal.NEUTRAL,
                            indicator = MovingAverage(50.0)
                        ),
                        TechnicalIndicator.WMA50 to AnalysisSignal.MovingAverageSignal(
                            signal = QuoteSignal.NEUTRAL,
                            indicator = MovingAverage(50.0)
                        ),
                        TechnicalIndicator.VWMA20 to AnalysisSignal.MovingAverageSignal(
                            signal = QuoteSignal.BUY,
                            indicator = MovingAverage(20.0)
                        ),
                        TechnicalIndicator.RSI to AnalysisSignal.OscillatorSignal(
                            signal = QuoteSignal.BUY,
                            indicator = Rsi(14.0)
                        ),
                        TechnicalIndicator.SRSI to AnalysisSignal.OscillatorSignal(
                            signal = QuoteSignal.SELL,
                            indicator = Srsi(14.0, 3.0)
                        ),
                        TechnicalIndicator.CCI to AnalysisSignal.OscillatorSignal(
                            signal = QuoteSignal.NEUTRAL,
                            indicator = Cci(20.0)
                        ),
                    )
                )
            ),
            signalSummary = mapOf(
                Interval.DAILY to Result.Success(
                    mapOf(
                        IndicatorType.MOVING_AVERAGE to AnalysisSignalSummary.MovingAverageSummary(
                            buy = 3,
                            sell = 2,
                            neutral = 1
                        ),
                        IndicatorType.OSCILLATOR to AnalysisSignalSummary.OscillatorSummary(
                            buy = 1,
                            sell = 1,
                            neutral = 1
                        ),
                        IndicatorType.TREND to AnalysisSignalSummary.TrendSummary(
                            buy = 1,
                            sell = 1,
                            neutral = 1
                        )
                    )
                )
            ),
            updateInterval = {}
        )
    }
}