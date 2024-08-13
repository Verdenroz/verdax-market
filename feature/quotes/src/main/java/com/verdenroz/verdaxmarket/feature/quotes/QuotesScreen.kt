package com.verdenroz.verdaxmarket.feature.quotes

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.verdenroz.verdaxmarket.core.common.enums.Interval
import com.verdenroz.verdaxmarket.core.common.enums.TimePeriod
import com.verdenroz.verdaxmarket.core.common.error.DataError
import com.verdenroz.verdaxmarket.core.common.result.Result
import com.verdenroz.verdaxmarket.core.designsystem.theme.ThemePreviews
import com.verdenroz.verdaxmarket.core.designsystem.theme.VxmTheme
import com.verdenroz.verdaxmarket.core.model.AnalysisSignal
import com.verdenroz.verdaxmarket.core.model.AnalysisSignalSummary
import com.verdenroz.verdaxmarket.core.model.FullQuoteData
import com.verdenroz.verdaxmarket.core.model.HistoricalData
import com.verdenroz.verdaxmarket.core.model.MarketSector
import com.verdenroz.verdaxmarket.core.model.News
import com.verdenroz.verdaxmarket.core.model.SimpleQuoteData
import com.verdenroz.verdaxmarket.core.model.indicators.IndicatorType
import com.verdenroz.verdaxmarket.core.model.indicators.TechnicalIndicator
import com.verdenroz.verdaxmarket.feature.quotes.components.QuoteChart
import com.verdenroz.verdaxmarket.feature.quotes.components.QuoteHeadline
import com.verdenroz.verdaxmarket.feature.quotes.components.QuotePerformance
import com.verdenroz.verdaxmarket.feature.quotes.components.QuoteScreenPager
import com.verdenroz.verdaxmarket.feature.quotes.components.QuoteTopBar
import com.verdenroz.verdaxmarket.feature.quotes.components.SimilarQuoteFeed

@Composable
internal fun QuotesRoute(
    symbol: String,
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    quotesViewModel: QuotesViewModel = hiltViewModel(
        creationCallback = { factory: QuotesViewModel.QuotesViewModelFactory ->
            factory.create(symbol)
        }
    ),
) {
    val quote by quotesViewModel.quote.collectAsStateWithLifecycle()
    val timeSeries by quotesViewModel.timeSeries.collectAsStateWithLifecycle()
    val similarQuotes by quotesViewModel.similarQuotes.collectAsStateWithLifecycle()
    val sectorPerformance by quotesViewModel.sectorPerformance.collectAsStateWithLifecycle()
    val news by quotesViewModel.news.collectAsStateWithLifecycle()
    val signals by quotesViewModel.signals.collectAsStateWithLifecycle()
    val signalSummary by quotesViewModel.signalSummary.collectAsStateWithLifecycle()
    val isWatchlisted by quotesViewModel.isWatchlisted.collectAsStateWithLifecycle()

    QuotesScreen(
        navController = navController,
        snackbarHostState = snackbarHostState,
        symbol = symbol,
        quote = quote,
        timeSeries = timeSeries,
        similarQuotes = similarQuotes,
        sectorPerformance = sectorPerformance,
        news = news,
        signals = signals,
        signalSummary = signalSummary,
        isWatchlisted = isWatchlisted,
        addToWatchlistLocal = quotesViewModel::addToWatchListLocal,
        addToWatchListNetwork = quotesViewModel::addToWatchlistNetwork,
        deleteFromWatchlist = quotesViewModel::deleteFromWatchlist,
        addToRecentQuotesLocal = quotesViewModel::addToRecentQuotesLocal,
        addToRecentQuotesNetwork = quotesViewModel::addToRecentQuotesNetwork
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun QuotesScreen(
    symbol: String,
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    quote: Result<FullQuoteData, DataError.Network>,
    timeSeries: Map<TimePeriod, Result<Map<String, HistoricalData>, DataError.Network>>,
    similarQuotes: Result<List<SimpleQuoteData>, DataError.Network>,
    sectorPerformance: Result<MarketSector?, DataError.Network>,
    news: Result<List<News>, DataError.Network>,
    signals: Map<Interval, Result<Map<TechnicalIndicator, AnalysisSignal>, DataError.Network>>,
    signalSummary: Map<Interval, Result<Map<IndicatorType, AnalysisSignalSummary>, DataError.Network>>,
    isWatchlisted: Boolean,
    addToWatchlistLocal: (SimpleQuoteData) -> Unit,
    addToWatchListNetwork: () -> Unit,
    deleteFromWatchlist: () -> Unit,
    addToRecentQuotesLocal: (SimpleQuoteData) -> Unit,
    addToRecentQuotesNetwork: () -> Unit,
) {
    Scaffold(
        topBar = {
            val quoteData = when (quote) {
                is Result.Success -> SimpleQuoteData(
                    symbol = quote.data.symbol,
                    name = quote.data.name,
                    price = quote.data.price,
                    change = quote.data.change,
                    percentChange = quote.data.percentChange
                )

                else -> null
            }
            QuoteTopBar(
                navController = navController,
                symbol = symbol,
                quote = quoteData,
                isWatchlisted = isWatchlisted,
                addToWatchlistLocal = addToWatchlistLocal,
                addToWatchlistNetwork = addToWatchListNetwork,
                deleteFromWatchlist = deleteFromWatchlist,
            )
        },
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
    ) { padding ->
        when (quote) {
            is Result.Loading -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.secondary)
                    Text(
                        text = stringResource(id = R.string.feature_quotes_loading),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimary,
                        letterSpacing = 1.5.sp
                    )
                }
            }

            is Result.Error -> {
                addToRecentQuotesNetwork()

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = stringResource(id = R.string.feature_quotes_error)
                    )
                    Text(
                        text = stringResource(id = R.string.feature_quotes_error),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimary,
                        letterSpacing = 1.5.sp
                    )
                }
            }

            is Result.Success -> {
                addToRecentQuotesLocal(
                    SimpleQuoteData(
                        symbol = quote.data.symbol,
                        name = quote.data.name,
                        price = quote.data.price,
                        change = quote.data.change,
                        percentChange = quote.data.percentChange
                    )
                )

                val listState = rememberLazyListState()
                Box(
                    modifier = Modifier.nestedScroll(rememberNestedScrollInteropConnection()),
                ) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        stickyHeader {
                            QuoteHeadline(
                                name = quote.data.name,
                                symbol = quote.data.symbol,
                                price = quote.data.price,
                                afterHoursPrice = quote.data.afterHoursPrice,
                                change = quote.data.change,
                                percentChange = quote.data.percentChange,
                                logo = quote.data.logo,
                            )
                        }

                        item {
                            QuoteChart(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 200.dp, max = 400.dp),
                                listState = listState,
                                snackbarHostState = snackbarHostState,
                                timeSeries = timeSeries,
                            )
                        }

                        quote.data.ytdReturn?.let {
                            item {
                                QuotePerformance(
                                    snackbarHost = snackbarHostState,
                                    symbol = quote.data.symbol,
                                    ytdReturn = it,
                                    yearReturn = quote.data.yearReturn,
                                    threeYearReturn = quote.data.threeYearReturn,
                                    fiveYearReturn = quote.data.fiveYearReturn,
                                    sector = quote.data.sector,
                                    sectorPerformance = sectorPerformance
                                )
                            }
                        }

                        item {
                            SimilarQuoteFeed(
                                symbol = quote.data.symbol,
                                similarQuotes = similarQuotes,
                                navController = navController,
                                snackbarHost = snackbarHostState
                            )
                        }

                        item {
                            QuoteScreenPager(
                                snackbarHostState = snackbarHostState,
                                quote = quote.data,
                                news = news,
                                signals = signals,
                                signalSummary = signalSummary
                            )
                        }
                    }
                }
            }
        }
    }
}

val previewFullQuoteData = FullQuoteData(
    name = "Apple Inc.",
    symbol = "AAPL",
    price = 113.2,
    afterHoursPrice = 179.74,
    change = "+1.23",
    percentChange = "+1.5%",
    high = 143.45,
    low = 110.45,
    open = 123.45,
    volume = 12345678,
    marketCap = "1.23T",
    pe = 12.34,
    eps = 1.23,
    beta = 1.23,
    yearHigh = 163.45,
    yearLow = 100.45,
    dividend = "1.23",
    yield = "1.23%",
    netAssets = null,
    nav = null,
    expenseRatio = null,
    category = "Blend",
    lastCapitalGain = "10.00",
    morningstarRating = "★★",
    morningstarRisk = "Low",
    holdingsTurnover = "1.23%",
    lastDividend = "0.05",
    inceptionDate = "Jan 1, 2022",
    exDividend = "Jan 1, 2022",
    earningsDate = "Jan 1, 2022",
    avgVolume = 12345678,
    sector = "Technology",
    industry = "Consumer Electronics",
    about = "Apple Inc. is an American multinational technology company that designs, manufactures, and markets consumer electronics, computer software, and online services. It is considered one of the Big Five companies in the U.S. information technology industry, along with Amazon, Google, Microsoft, and Facebook.",
    ytdReturn = "1.23%",
    yearReturn = "1.23%",
    threeYearReturn = "1.23%",
    fiveYearReturn = "1.23%",
    logo = "https://logo.clearbit.com/apple.com"
)

@ThemePreviews
@Composable
private fun PreviewQuoteScreen() {
    VxmTheme {
        QuotesScreen(
            symbol = "AAPL",
            navController = rememberNavController(),
            snackbarHostState = SnackbarHostState(),
            quote = Result.Success(previewFullQuoteData),
            timeSeries = mapOf(TimePeriod.YEAR_TO_DATE to Result.Success(emptyMap())),
            similarQuotes = Result.Success(
                listOf(
                    SimpleQuoteData(
                        symbol = "AAPL",
                        name = "Apple Inc.",
                        price = 120.00,
                        change = "+1.00",
                        percentChange = "+0.5%"
                    ),
                    SimpleQuoteData(
                        symbol = "NVDA",
                        name = "NVIDIA",
                        price = 120.00,
                        change = "+1.00",
                        percentChange = "+0.5%"
                    ),
                    SimpleQuoteData(
                        symbol = "TSLA",
                        name = "Tesla",
                        price = 120.00,
                        change = "+1.00",
                        percentChange = "+0.5%"
                    ),
                    SimpleQuoteData(
                        symbol = "NFLX",
                        name = "Netflix",
                        price = 120.00,
                        change = "+1.00",
                        percentChange = "+0.5%"
                    ),
                )
            ),
            sectorPerformance = Result.Success(
                MarketSector(
                    sector = "Technology",
                    dayReturn = "+1.5%",
                    yearReturn = "+1.5%",
                    threeYearReturn = "+1.5%",
                    fiveYearReturn = "+1.5%",
                    ytdReturn = "+1.5%"
                )
            ),
            news = Result.Success(emptyList()),
            signals = mapOf(Interval.DAILY to Result.Success(emptyMap())),
            signalSummary = mapOf(Interval.DAILY to Result.Success(emptyMap())),
            isWatchlisted = false,
            addToWatchlistLocal = { },
            addToWatchListNetwork = { },
            deleteFromWatchlist = {},
            addToRecentQuotesLocal = { },
            addToRecentQuotesNetwork = { }
        )
    }
}



