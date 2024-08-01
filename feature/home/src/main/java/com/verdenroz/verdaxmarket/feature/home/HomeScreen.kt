package com.verdenroz.verdaxmarket.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.verdenroz.verdaxmarket.core.common.error.DataError
import com.verdenroz.verdaxmarket.core.common.result.Result
import com.verdenroz.verdaxmarket.core.designsystem.theme.ThemePreviews
import com.verdenroz.verdaxmarket.core.designsystem.theme.VxmTheme
import com.verdenroz.verdaxmarket.core.model.MarketIndex
import com.verdenroz.verdaxmarket.core.model.MarketMover
import com.verdenroz.verdaxmarket.core.model.MarketSector
import com.verdenroz.verdaxmarket.core.model.News
import kotlinx.coroutines.delay

@Composable
internal fun HomeRoute(
    navController: NavController,
    snackbarHost: SnackbarHostState,
    homeViewModel: HomeViewModel = hiltViewModel(),
) {
    val sectors by homeViewModel.sectors.collectAsState()
    val headlines by homeViewModel.headlines.collectAsState()
    val indices by homeViewModel.indices.collectAsState()
    val actives by homeViewModel.actives.collectAsState()
    val losers by homeViewModel.losers.collectAsState()
    val gainers by homeViewModel.gainers.collectAsState()

    HomeScreen(
        navController = navController,
        snackbarHost = snackbarHost,
        sectors = sectors,
        headlines = headlines,
        indices = indices,
        actives = actives,
        losers = losers,
        gainers = gainers,
        refresh = homeViewModel::refresh
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeScreen(
    navController: NavController,
    snackbarHost: SnackbarHostState,
    sectors: Result<List<MarketSector>, DataError.Network>,
    indices: Result<List<MarketIndex>, DataError.Network>,
    headlines: Result<List<News>, DataError.Network>,
    actives: Result<List<MarketMover>, DataError.Network>,
    losers: Result<List<MarketMover>, DataError.Network>,
    gainers: Result<List<MarketMover>, DataError.Network>,
    refresh: () -> Unit,
) {
    val pullRefreshState = rememberPullToRefreshState()
    if (pullRefreshState.isRefreshing) {
        LaunchedEffect(true) {
            refresh()
            delay(500)
        }
    }
    val listState = rememberLazyListState()

    Box(
        modifier = Modifier.nestedScroll(pullRefreshState.nestedScrollConnection),
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                MarketIndices(
                    indices = indices,
                    snackbarHost = snackbarHost,
                )
            }
            item {
                MarketSectors(
                    sectors = sectors,
                    snackbarHost = snackbarHost,
                )
            }
            item {
                NewsFeed(
                    headlines = headlines,
                    snackbarHost = snackbarHost,
                )
            }
            item {
                MarketMovers(
                    listState = listState,
                    navController = navController,
                    snackbarHost = snackbarHost,
                    actives = actives,
                    losers = losers,
                    gainers = gainers,
                )
            }
        }
        PullToRefreshContainer(
            state = pullRefreshState,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-16).dp)
        )
    }
}

@ThemePreviews
@Composable
private fun PreviewSuccessHomeScreen() {
    val sectors = listOf(
        MarketSector(
            sector = "Technology",
            dayReturn = "0.5%",
            ytdReturn = "1.2%",
            yearReturn = "3.4%",
            threeYearReturn = "-5.6%",
            fiveYearReturn = "-7.8%"
        ),
        MarketSector(
            sector = "Consumer Cyclical",
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
            navController = rememberNavController(),
            snackbarHost = SnackbarHostState(),
            sectors = Result.Success(sectors),
            headlines = Result.Success(headlines),
            indices = Result.Success(indices),
            actives = Result.Success(movers),
            losers = Result.Success(movers),
            gainers = Result.Success(movers),
            refresh = {}
        )
    }
}

@ThemePreviews
@Composable
private fun PreviewLoadingHomeScreen() {
    VxmTheme {
        HomeScreen(
            navController = rememberNavController(),
            snackbarHost = SnackbarHostState(),
            sectors = Result.Loading(),
            indices = Result.Loading(),
            headlines = Result.Loading(),
            actives = Result.Loading(),
            losers = Result.Loading(),
            gainers = Result.Loading(),
            refresh = {}
        )
    }
}