package com.verdenroz.verdaxmarket.feature.quotes.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.verdenroz.verdaxmarket.core.designsystem.theme.ThemePreviews
import com.verdenroz.verdaxmarket.core.designsystem.theme.VxmTheme
import com.verdenroz.verdaxmarket.core.designsystem.theme.negativeTextColor
import com.verdenroz.verdaxmarket.core.designsystem.theme.positiveTextColor
import com.verdenroz.verdaxmarket.core.model.MarketSector
import com.verdenroz.verdaxmarket.feature.quotes.R

@Composable
internal fun QuotePerformance(
    symbol: String,
    ytdReturn: String,
    yearReturn: String?,
    threeYearReturn: String?,
    fiveYearReturn: String?,
    sector: String?,
    sectorPerformance: MarketSector?
) {
    if (sectorPerformance != null) {
        Column(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.feature_quotes_performance) + ": $symbol",
                style = MaterialTheme.typography.titleMedium,
                letterSpacing = 1.5.sp,
                modifier = Modifier.padding(8.dp)
            )
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    8.dp,
                    Alignment.CenterHorizontally
                ),
            ) {
                ytdReturn.let {
                    item {
                        PerformanceCard(
                            label = stringResource(id = R.string.feature_quotes_ytd_return),
                            symbol = symbol,
                            sector = when (sector) {
                                "Technology" -> stringResource(id = R.string.feature_quotes_sector_technology)
                                "Healthcare" -> stringResource(id = R.string.feature_quotes_sector_healthcare)
                                "Financial Services" -> stringResource(id = R.string.feature_quotes_sector_financial_services)
                                "Consumer Cyclical" -> stringResource(id = R.string.feature_quotes_sector_consumer_cyclical)
                                "Industrials" -> stringResource(id = R.string.feature_quotes_sector_industrials)
                                "Consumer Defensive" -> stringResource(id = R.string.feature_quotes_sector_consumer_defensive)
                                "Energy" -> stringResource(id = R.string.feature_quotes_sector_energy)
                                "Real Estate" -> stringResource(id = R.string.feature_quotes_sector_real_estate)
                                "Utilities" -> stringResource(id = R.string.feature_quotes_sector_utilities)
                                "Basic Materials" -> stringResource(id = R.string.feature_quotes_sector_basic_materials)
                                "Communication Services" -> stringResource(id = R.string.feature_quotes_sector_communication_services)
                                else -> sector
                            },
                            stockPerformance = it,
                            sectorPerformance = sectorPerformance.ytdReturn
                        )
                    }
                }
                yearReturn?.let {
                    item {
                        PerformanceCard(
                            label = stringResource(id = R.string.feature_quotes_one_year_return),
                            symbol = symbol,
                            sector = when (sector) {
                                "Technology" -> stringResource(id = R.string.feature_quotes_sector_technology)
                                "Healthcare" -> stringResource(id = R.string.feature_quotes_sector_healthcare)
                                "Financial Services" -> stringResource(id = R.string.feature_quotes_sector_financial_services)
                                "Consumer Cyclical" -> stringResource(id = R.string.feature_quotes_sector_consumer_cyclical)
                                "Industrials" -> stringResource(id = R.string.feature_quotes_sector_industrials)
                                "Consumer Defensive" -> stringResource(id = R.string.feature_quotes_sector_consumer_defensive)
                                "Energy" -> stringResource(id = R.string.feature_quotes_sector_energy)
                                "Real Estate" -> stringResource(id = R.string.feature_quotes_sector_real_estate)
                                "Utilities" -> stringResource(id = R.string.feature_quotes_sector_utilities)
                                "Basic Materials" -> stringResource(id = R.string.feature_quotes_sector_basic_materials)
                                "Communication Services" -> stringResource(id = R.string.feature_quotes_sector_communication_services)
                                else -> sector
                            },
                            stockPerformance = it,
                            sectorPerformance = sectorPerformance.yearReturn
                        )
                    }

                }
                threeYearReturn?.let {
                    item {
                        PerformanceCard(
                            label = stringResource(id = R.string.feature_quotes_three_year_return),
                            symbol = symbol,
                            sector = when (sector) {
                                "Technology" -> stringResource(id = R.string.feature_quotes_sector_technology)
                                "Healthcare" -> stringResource(id = R.string.feature_quotes_sector_healthcare)
                                "Financial Services" -> stringResource(id = R.string.feature_quotes_sector_financial_services)
                                "Consumer Cyclical" -> stringResource(id = R.string.feature_quotes_sector_consumer_cyclical)
                                "Industrials" -> stringResource(id = R.string.feature_quotes_sector_industrials)
                                "Consumer Defensive" -> stringResource(id = R.string.feature_quotes_sector_consumer_defensive)
                                "Energy" -> stringResource(id = R.string.feature_quotes_sector_energy)
                                "Real Estate" -> stringResource(id = R.string.feature_quotes_sector_real_estate)
                                "Utilities" -> stringResource(id = R.string.feature_quotes_sector_utilities)
                                "Basic Materials" -> stringResource(id = R.string.feature_quotes_sector_basic_materials)
                                "Communication Services" -> stringResource(id = R.string.feature_quotes_sector_communication_services)
                                else -> sector
                            },
                            stockPerformance = it,
                            sectorPerformance = sectorPerformance.threeYearReturn
                        )
                    }

                }
                fiveYearReturn?.let {
                    item {
                        PerformanceCard(
                            label = stringResource(id = R.string.feature_quotes_five_year_return),
                            symbol = symbol,
                            sector = when (sector) {
                                "Technology" -> stringResource(id = R.string.feature_quotes_sector_technology)
                                "Healthcare" -> stringResource(id = R.string.feature_quotes_sector_healthcare)
                                "Financial Services" -> stringResource(id = R.string.feature_quotes_sector_financial_services)
                                "Consumer Cyclical" -> stringResource(id = R.string.feature_quotes_sector_consumer_cyclical)
                                "Industrials" -> stringResource(id = R.string.feature_quotes_sector_industrials)
                                "Consumer Defensive" -> stringResource(id = R.string.feature_quotes_sector_consumer_defensive)
                                "Energy" -> stringResource(id = R.string.feature_quotes_sector_energy)
                                "Real Estate" -> stringResource(id = R.string.feature_quotes_sector_real_estate)
                                "Utilities" -> stringResource(id = R.string.feature_quotes_sector_utilities)
                                "Basic Materials" -> stringResource(id = R.string.feature_quotes_sector_basic_materials)
                                "Communication Services" -> stringResource(id = R.string.feature_quotes_sector_communication_services)
                                else -> sector
                            },
                            stockPerformance = it,
                            sectorPerformance = sectorPerformance.fiveYearReturn
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PerformanceCard(
    label: String,
    symbol: String,
    sector: String?,
    stockPerformance: String,
    sectorPerformance: String?
) {
    Card(
        modifier = Modifier.size(300.dp, 125.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 8.dp, horizontal = 16.dp),
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                letterSpacing = 1.5.sp
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = symbol,
                        style = MaterialTheme.typography.titleSmall,
                        letterSpacing = 1.25.sp
                    )
                    Text(
                        text = stockPerformance,
                        style = MaterialTheme.typography.labelLarge,
                        color = if (stockPerformance.contains('+')) positiveTextColor else negativeTextColor
                    )
                }
                if (sector != null && sectorPerformance != null) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = sector,
                            style = MaterialTheme.typography.titleSmall,
                            letterSpacing = 1.25.sp
                        )
                        Text(
                            text = sectorPerformance,
                            style = MaterialTheme.typography.labelLarge,
                            color = if (sectorPerformance.contains('+')) positiveTextColor else negativeTextColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StockPerformanceSkeleton(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.surfaceContainer
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
    ) {
        repeat(3) {
            item(key = it) {
                Card(
                    modifier = modifier.size(300.dp, 125.dp),
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
private fun PreviewPerformanceCard() {
    VxmTheme {
        PerformanceCard(
            label = "YTD Return",
            symbol = "AAPL",
            sector = "Communication Services",
            stockPerformance = "-10.00%",
            sectorPerformance = "-5.00%"
        )
    }
}

@ThemePreviews
@Composable
private fun PreviewStockPerformanceSkeleton() {
    VxmTheme {
        StockPerformanceSkeleton()
    }
}
