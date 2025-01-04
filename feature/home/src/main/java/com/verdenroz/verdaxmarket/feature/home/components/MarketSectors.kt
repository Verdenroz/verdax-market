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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.rememberCoroutineScope
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
import com.verdenroz.verdaxmarket.core.designsystem.theme.ThemePreviews
import com.verdenroz.verdaxmarket.core.designsystem.theme.VxmTheme
import com.verdenroz.verdaxmarket.core.designsystem.theme.getNegativeTextColor
import com.verdenroz.verdaxmarket.core.designsystem.theme.getPositiveTextColor
import com.verdenroz.verdaxmarket.core.designsystem.util.UiText
import com.verdenroz.verdaxmarket.core.designsystem.util.asUiText
import com.verdenroz.verdaxmarket.core.model.HistoricalData
import com.verdenroz.verdaxmarket.core.model.MarketSector
import com.verdenroz.verdaxmarket.core.model.Sector
import com.verdenroz.verdaxmarket.core.model.toDisplayName
import com.verdenroz.verdaxmarket.feature.home.R
import kotlinx.coroutines.launch

@Composable
internal fun MarketSectors(
    onShowSnackbar: suspend (String, String?, SnackbarDuration) -> Boolean,
    sectors: Result<List<MarketSector>, DataError.Network>,
    sectorTimeSeries: Map<Sector, Result<Map<String, HistoricalData>, DataError.Network>>,
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(id = R.string.feature_home_sector_performance),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.fillMaxWidth()
        )
        when (sectors) {
            is Result.Loading -> {
                MarketSectorsSkeleton()
            }

            is Result.Error -> {
                MarketSectorsSkeleton()

                LaunchedEffect(sectors.error) {
                    onShowSnackbar(
                        sectors.error.asUiText().asString(context),
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
                        items = sectors.data,
                        key = { sector -> sector.sector }
                    ) { sector ->
                        MarketSectorCard(
                            sector = sector,
                            timeSeries = sectorTimeSeries[sector.sector]
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketSectorCard(
    sector: MarketSector,
    timeSeries: Result<Map<String, HistoricalData>, DataError.Network>?,
    modifier: Modifier = Modifier
) {
    val tooltipState = rememberTooltipState()
    val scope = rememberCoroutineScope()

    TooltipBox(
        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
        tooltip = {
            PlainTooltip {
                Text(sector.sector.toDisplayName())
            }
        },
        state = tooltipState
    ) {
        Card(
            modifier = modifier
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
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = sector.sector.toDisplayName(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    when (timeSeries) {
                        is Result.Success -> {
                            Sparkline(
                                timeSeries = timeSeries.data,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        is Result.Loading -> {
                            LinearProgressIndicator()
                        }

                        is Result.Error, null -> {
                            // no sparkline
                        }
                    }
                }

                if (timeSeries is Result.Success) {
                    Text(
                        text = stringResource(id = R.string.feature_home_sector_trend),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }

                HorizontalDivider()
                PerformanceRow(
                    title = stringResource(id = R.string.feature_home_sector_return),
                    value = sector.yearReturn,
                    color = if (sector.yearReturn.contains("-")) getNegativeTextColor() else getPositiveTextColor()
                )
            }
        }
    }
}

@ThemePreviews
@Composable
private fun PreviewMarketSectors() {
    VxmTheme {
        Column {
            MarketSectorCard(
                sector = MarketSector(
                    sector = Sector.TECHNOLOGY,
                    dayReturn = "+1.23%",
                    ytdReturn = "+4.56%",
                    yearReturn = "+12.34%",
                    threeYearReturn = "+56.78%",
                    fiveYearReturn = "+90.12%",
                ),
                timeSeries = null
            )
            MarketSectorCard(
                sector = MarketSector(
                    sector = Sector.TECHNOLOGY,
                    dayReturn = "+1.23%",
                    ytdReturn = "+4.56%",
                    yearReturn = "+12.34%",
                    threeYearReturn = "+56.78%",
                    fiveYearReturn = "+90.12%",
                ),
                timeSeries = Result.Loading()
            )
        }
    }
}

@Composable
fun MarketSectorsSkeleton(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primaryContainer
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(5) {
            item(key = it) {
                Card(
                    modifier = modifier.size(225.dp, 150.dp),
                    colors = CardDefaults.cardColors(containerColor = color)
                ) {
                    // skeleton
                }
            }
        }
    }
}

@ThemePreviews
@Composable
private fun PreviewMarketSectorsSkeleton() {
    VxmTheme {
        MarketSectorsSkeleton()
    }
}

@Composable
private fun PerformanceRow(
    title: String,
    value: String,
    color: Color,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = color
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = color
        )
    }
}
