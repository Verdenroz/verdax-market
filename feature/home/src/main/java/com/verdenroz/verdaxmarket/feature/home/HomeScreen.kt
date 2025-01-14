package com.verdenroz.verdaxmarket.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.verdenroz.verdaxmarket.core.common.error.DataError
import com.verdenroz.verdaxmarket.core.common.result.Result
import com.verdenroz.verdaxmarket.core.designsystem.theme.ThemePreviews
import com.verdenroz.verdaxmarket.core.designsystem.theme.VxmTheme
import com.verdenroz.verdaxmarket.core.model.HistoricalData
import com.verdenroz.verdaxmarket.core.model.MarketIndex
import com.verdenroz.verdaxmarket.core.model.MarketMover
import com.verdenroz.verdaxmarket.core.model.MarketSector
import com.verdenroz.verdaxmarket.core.model.News
import com.verdenroz.verdaxmarket.core.model.Sector
import com.verdenroz.verdaxmarket.feature.home.components.MarketIndices
import com.verdenroz.verdaxmarket.feature.home.components.MarketMovers
import com.verdenroz.verdaxmarket.feature.home.components.MarketSectors
import com.verdenroz.verdaxmarket.feature.home.components.NewsFeed

@Composable
internal fun HomeRoute(
    onNavigateToQuote: (String) -> Unit,
    onShowSnackbar: suspend (String, String?, SnackbarDuration) -> Boolean,
    homeViewModel: HomeViewModel = hiltViewModel(),
) {
    val indices by homeViewModel.indices.collectAsStateWithLifecycle()
    val indexTimeSeries by homeViewModel.indexTimeSeries.collectAsStateWithLifecycle()
    val sectors by homeViewModel.sectors.collectAsStateWithLifecycle()
    val sectorTimeSeries by homeViewModel.sectorTimeSeries.collectAsStateWithLifecycle()
    val headlines by homeViewModel.headlines.collectAsStateWithLifecycle()
    val actives by homeViewModel.actives.collectAsStateWithLifecycle()
    val losers by homeViewModel.losers.collectAsStateWithLifecycle()
    val gainers by homeViewModel.gainers.collectAsStateWithLifecycle()
    HomeScreen(
        onNavigateToQuote = onNavigateToQuote,
        onShowSnackbar = onShowSnackbar,
        indices = indices,
        indexTimeSeries = indexTimeSeries,
        sectors = sectors,
        sectorTimeSeries = sectorTimeSeries,
        headlines = headlines,
        actives = actives,
        losers = losers,
        gainers = gainers,
    )
}

@Composable
internal fun HomeScreen(
    indices: Result<List<MarketIndex>, DataError.Network>,
    indexTimeSeries: Map<String, Result<Map<String, HistoricalData>, DataError.Network>>,
    sectors: Result<List<MarketSector>, DataError.Network>,
    sectorTimeSeries: Map<Sector, Result<Map<String, HistoricalData>, DataError.Network>>,
    headlines: Result<List<News>, DataError.Network>,
    actives: Result<List<MarketMover>, DataError.Network>,
    losers: Result<List<MarketMover>, DataError.Network>,
    gainers: Result<List<MarketMover>, DataError.Network>,
    onNavigateToQuote: (String) -> Unit,
    onShowSnackbar: suspend (String, String?, SnackbarDuration) -> Boolean,
) {
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item(key = "MarketIndices") {
            MarketIndices(
                indices = indices,
                indexTimeSeries = indexTimeSeries,
                onShowSnackbar = onShowSnackbar,
            )
        }
        item(key = "MarketSectors") {
            MarketSectors(
                sectors = sectors,
                sectorTimeSeries = sectorTimeSeries,
                onShowSnackbar = onShowSnackbar,
            )
        }
        item(key = "NewsFeed") {
            NewsFeed(
                headlines = headlines,
                onShowSnackbar = onShowSnackbar,
            )
        }
        item(key = "MarketMovers") {
            MarketMovers(
                listState = listState,
                onNavigateToQuote = onNavigateToQuote,
                onShowSnackbar = onShowSnackbar,
                actives = actives,
                losers = losers,
                gainers = gainers,
            )
        }
    }
}

@ThemePreviews
@Composable
private fun PreviewSuccessHomeScreen() {
    val sectors = listOf(
        MarketSector(
            sector = Sector.TECHNOLOGY,
            dayReturn = "0.5%",
            ytdReturn = "1.2%",
            yearReturn = "3.4%",
            threeYearReturn = "-5.6%",
            fiveYearReturn = "-7.8%"
        ),
        MarketSector(
            sector = Sector.CONSUMER_CYCLICAL,
            dayReturn = "-0.5%",
            ytdReturn = "-1.2%",
            yearReturn = "-3.4%",
            threeYearReturn = "-5.6%",
            fiveYearReturn = "-7.8%"
        )
    )

    val headlines = listOf(
        News(
            title = "Title",
            source = "Yahoo Finance",
            link = "Link",
            img = "https://cdn.snapi.dev/images/v1/t/w/gen28-2490436-2550324.jpg",
            time = "2 hours ago"
        ),
        News(
            title = "Title",
            source = "Yahoo Finance",
            link = "Link1",
            img = "https://cdn.snapi.dev/images/v1/t/w/gen28-2490436-2550324.jpg",
            time = "2 hours ago"
        )
    )

    val indices = listOf(
        MarketIndex(
            name = "Dow Jones",
            value = "34000",
            change = "+50.00",
            percentChange = "+0.5%"
        ),
        MarketIndex(
            name = "S&P 500",
            value = "34000",
            change = "-50.00",
            percentChange = "-0.5%"
        ),
        MarketIndex(
            name = "NASDAQ",
            value = "34000",
            change = "+50.00",
            percentChange = "+0.5%"
        ),
    )

    val movers = listOf(
        MarketMover(
            symbol = "AAPL",
            name = "Apple Inc.",
            price = "120.00",
            change = "+1.00",
            percentChange = "+0.5%"
        ),
        MarketMover(
            symbol = "NVDA",
            name = "NVIDIA",
            price = "120.00",
            change = "+1.00",
            percentChange = "+0.5%"
        ),
        MarketMover(
            symbol = "TSLA",
            name = "Tesla",
            price = "120.00",
            change = "+1.00",
            percentChange = "+0.5%"
        ),
        MarketMover(
            symbol = "NFLX",
            name = "Netflix",
            price = "120.00",
            change = "+1.00",
            percentChange = "+0.5%"
        ),
    )

    VxmTheme {
        HomeScreen(
            onNavigateToQuote = {},
            onShowSnackbar = { _, _, _ -> true },
            indices = Result.Success(indices),
            indexTimeSeries = emptyMap(),
            sectors = Result.Success(sectors),
            sectorTimeSeries = emptyMap(),
            headlines = Result.Success(headlines),
            actives = Result.Success(movers),
            losers = Result.Success(movers),
            gainers = Result.Success(movers),
        )
    }
}

@ThemePreviews
@Composable
private fun PreviewLoadingHomeScreen() {
    VxmTheme {
        HomeScreen(
            onNavigateToQuote = {},
            onShowSnackbar = { _, _, _ -> true },
            indices = Result.Loading(),
            indexTimeSeries = mapOf(
                "Dow Jones" to Result.Loading(),
                "S&P 500" to Result.Loading(),
                "NASDAQ" to Result.Loading()
            ),
            sectors = Result.Loading(),
            sectorTimeSeries = mapOf(
                Sector.TECHNOLOGY to Result.Loading(),
                Sector.CONSUMER_CYCLICAL to Result.Loading()
            ),
            headlines = Result.Loading(),
            actives = Result.Loading(),
            losers = Result.Loading(),
            gainers = Result.Loading(),
        )
    }
}