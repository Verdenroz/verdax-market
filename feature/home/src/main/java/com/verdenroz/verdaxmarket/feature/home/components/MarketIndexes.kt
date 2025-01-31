package com.verdenroz.verdaxmarket.feature.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.verdenroz.verdaxmarket.core.common.error.DataError
import com.verdenroz.verdaxmarket.core.common.result.Result
import com.verdenroz.verdaxmarket.core.designsystem.icons.VxmIcons
import com.verdenroz.verdaxmarket.core.designsystem.theme.ThemePreviews
import com.verdenroz.verdaxmarket.core.designsystem.theme.VxmTheme
import com.verdenroz.verdaxmarket.core.designsystem.theme.getNegativeTextColor
import com.verdenroz.verdaxmarket.core.designsystem.theme.getPositiveTextColor
import com.verdenroz.verdaxmarket.core.designsystem.util.UiText
import com.verdenroz.verdaxmarket.core.designsystem.util.asUiText
import com.verdenroz.verdaxmarket.core.model.HistoricalData
import com.verdenroz.verdaxmarket.core.model.MarketIndex
import com.verdenroz.verdaxmarket.core.model.enums.TimePeriodPreference
import com.verdenroz.verdaxmarket.feature.home.R
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@Composable
internal fun MarketIndices(
    indices: Result<List<MarketIndex>, DataError>,
    indexTimeSeries: Map<String, Result<Map<String, HistoricalData>, DataError>>,
    indexTimePeriodPreference: TimePeriodPreference,
    onTimePeriodChange: (TimePeriodPreference) -> Unit,
    onShowSnackbar: suspend (String, String?, SnackbarDuration) -> Boolean,
) {
    val context = LocalContext.current
    var showMenu by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.feature_home_market_performance),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onPrimary,
            )

            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(
                        imageVector = VxmIcons.More,
                        contentDescription = stringResource(R.string.feature_home_change_time_period),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    TimePeriodPreference.entries.forEach { timePeriod ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = when (timePeriod) {
                                        TimePeriodPreference.ONE_DAY -> stringResource(R.string.feature_home_index_one_day)
                                        TimePeriodPreference.FIVE_DAY -> stringResource(R.string.feature_home_index_five_day)
                                        TimePeriodPreference.ONE_MONTH -> stringResource(R.string.feature_home_index_one_month)
                                        TimePeriodPreference.SIX_MONTH -> stringResource(R.string.feature_home_index_six_month)
                                        TimePeriodPreference.YEAR_TO_DATE -> stringResource(R.string.feature_home_index_ytd)
                                        TimePeriodPreference.ONE_YEAR -> stringResource(R.string.feature_home_index_one_year)
                                        TimePeriodPreference.FIVE_YEAR -> stringResource(R.string.feature_home_index_five_year)
                                    }
                                )
                            },
                            onClick = {
                                onTimePeriodChange(timePeriod)
                                showMenu = false
                            },
                            leadingIcon = {
                                if (timePeriod == indexTimePeriodPreference) {
                                    Icon(
                                        imageVector = VxmIcons.CheckCircle,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.secondary
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }

        when (indices) {
            is Result.Loading -> {
                MarketIndexSkeleton()
            }

            is Result.Error -> {
                MarketIndexSkeleton()

                LaunchedEffect(indices.error) {
                    onShowSnackbar(
                        indices.error.asUiText().asString(context),
                        UiText.StringResource(R.string.feature_home_dismiss).asString(context),
                        SnackbarDuration.Short
                    )
                }
            }

            is Result.Success -> {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = indices.data,
                        key = { index -> index.name }
                    ) { index ->
                        MarketIndexCard(
                            index = index,
                            timeSeries = indexTimeSeries[index.name],
                            indexTimePeriodPreference = indexTimePeriodPreference
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketIndexCard(
    index: MarketIndex,
    timeSeries: Result<Map<String, HistoricalData>, DataError>?,
    indexTimePeriodPreference: TimePeriodPreference,
) {
    val tooltipState = rememberTooltipState()
    val scope = rememberCoroutineScope()
    TooltipBox(
        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
        tooltip = {
            PlainTooltip {
                Text(index.name)
            }
        },
        state = tooltipState
    ) {
        Card(
            modifier = Modifier
                .size(225.dp, 150.dp)
                .clickable { scope.launch { tooltipState.show() } },
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = index.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = if (timeSeries is Result.Success) Alignment.TopStart else Alignment.Center
                ) {
                    when (timeSeries) {
                        is Result.Success -> {
                            Sparkline(
                                timeSeries = timeSeries.data,
                                // Time series data is sorted in descending order so the first value is the latest
                                color = if (timeSeries.data.values.first().close > timeSeries.data.values.last().close) getPositiveTextColor() else getNegativeTextColor(),
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        is Result.Loading, null -> {
                            LinearProgressIndicator()
                        }

                        is Result.Error -> {
                            // No data
                        }
                    }
                }

                if (timeSeries is Result.Success) {
                    Text(
                        text = when(indexTimePeriodPreference) {
                            TimePeriodPreference.ONE_DAY -> stringResource(R.string.feature_home_trend_1d)
                            TimePeriodPreference.FIVE_DAY -> stringResource(R.string.feature_home_trend_5d)
                            TimePeriodPreference.ONE_MONTH -> stringResource(R.string.feature_home_trend_1m)
                            TimePeriodPreference.SIX_MONTH -> stringResource(R.string.feature_home_trend_6m)
                            TimePeriodPreference.YEAR_TO_DATE -> stringResource(R.string.feature_home_trend_ytd)
                            TimePeriodPreference.ONE_YEAR -> stringResource(R.string.feature_home_trend_1y)
                            TimePeriodPreference.FIVE_YEAR -> stringResource(R.string.feature_home_trend_5y)
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = NumberFormat.getNumberInstance(Locale.US).format(index.value.toDouble()),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = index.change,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (index.change.contains('+')) getPositiveTextColor() else getNegativeTextColor()
                    )
                    Text(
                        text = index.percentChange,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (index.change.contains('+')) getPositiveTextColor() else getNegativeTextColor()
                    )
                }
            }
        }
    }
}

@ThemePreviews
@Composable
private fun PreviewMarketIndexCard() {
    VxmTheme {
        MarketIndexCard(
            MarketIndex(
                name = "Dow Jones",
                value = "100.0",
                change = "+100.0",
                percentChange = "+100%"
            ),
            timeSeries = null,
            indexTimePeriodPreference = TimePeriodPreference.ONE_DAY
        )
    }
}

@Composable
fun MarketIndexSkeleton(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primaryContainer
) {
    LazyRow(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(5) {
            item(key = it) {
                Card(
                    modifier = Modifier.size(225.dp, 150.dp),
                    colors = CardDefaults.cardColors(containerColor = color)
                ) {
                    // Content of the card, leave it empty for skeleton
                }
            }
        }
    }
}

@ThemePreviews
@Composable
private fun PreviewMarketIndexSkeleton() {
    VxmTheme {
        MarketIndexSkeleton()
    }
}
