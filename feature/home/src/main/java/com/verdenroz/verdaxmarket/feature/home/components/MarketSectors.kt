package com.verdenroz.verdaxmarket.feature.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
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
import com.verdenroz.verdaxmarket.core.designsystem.theme.negativeTextColor
import com.verdenroz.verdaxmarket.core.designsystem.theme.positiveTextColor
import com.verdenroz.verdaxmarket.core.designsystem.util.UiText
import com.verdenroz.verdaxmarket.core.designsystem.util.asUiText
import com.verdenroz.verdaxmarket.core.model.MarketSector
import com.verdenroz.verdaxmarket.feature.home.R
import kotlinx.coroutines.launch

@Composable
fun MarketSectors(
    sectors: Result<List<MarketSector>, DataError.Network>,
    snackbarHostState: SnackbarHostState,
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
                    snackbarHostState.showSnackbar(
                        message = sectors.error.asUiText().asString(context),
                        actionLabel = UiText.StringResource(R.string.feature_home_dismiss)
                            .asString(context),
                        duration = SnackbarDuration.Short
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
                        MarketSectorCard(sector)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketSectorCard(sector: MarketSector) {
    val tooltipState = rememberTooltipState()
    val scope = rememberCoroutineScope()
    TooltipBox(
        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
        tooltip = {
            PlainTooltip {
                Text(sector.sector)
            }
        },
        state = tooltipState
    ) {
        Card(
            modifier = Modifier
                .size(175.dp, 100.dp)
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
                verticalArrangement = Arrangement.SpaceAround
            ) {
                Text(
                    text = sector.sector,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
                Column {
                    PerformanceRow(
                        title = stringResource(id = R.string.feature_home_day_return),
                        value = sector.dayReturn,
                        color = if (sector.dayReturn.contains("-")) negativeTextColor else positiveTextColor
                    )
                    PerformanceRow(
                        title = stringResource(id = R.string.feature_home_ytd_return),
                        value = sector.ytdReturn,
                        color = if (sector.dayReturn.contains("-")) negativeTextColor else positiveTextColor
                    )
                    PerformanceRow(
                        title = stringResource(id = R.string.feature_home_three_year_return),
                        value = sector.threeYearReturn,
                        color = if (sector.dayReturn.contains("-")) negativeTextColor else positiveTextColor
                    )
                }
            }
        }
    }
}

@Composable
fun PerformanceRow(
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

@ThemePreviews
@Composable
private fun PreviewMarketSectors() {
    VxmTheme {
        MarketSectorCard(
            sector = MarketSector(
                sector = "Technology",
                dayReturn = "+1.23%",
                ytdReturn = "+4.56%",
                yearReturn = "+12.34%",
                threeYearReturn = "+56.78%",
                fiveYearReturn = "+90.12%",
            )
        )
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
                    modifier = modifier.size(175.dp, 100.dp),
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