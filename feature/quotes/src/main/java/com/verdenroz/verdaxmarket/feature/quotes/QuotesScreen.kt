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
import androidx.compose.material3.SnackbarDuration
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
import com.verdenroz.verdaxmarket.core.model.Profile
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
    onNavigateBack: () -> Unit,
    onNavigateToQuote: (String) -> Unit,
    onShowSnackbar: suspend (String, String?, SnackbarDuration) -> Boolean,
    quotesViewModel: QuotesViewModel = hiltViewModel(
        creationCallback = { factory: QuotesViewModel.QuotesViewModelFactory ->
            factory.create(symbol)
        }
    ),
) {
    val profile by quotesViewModel.profile.collectAsStateWithLifecycle()
    val timeSeries by quotesViewModel.timeSeries.collectAsStateWithLifecycle()
    val signals by quotesViewModel.signals.collectAsStateWithLifecycle()
    val signalSummary by quotesViewModel.signalSummary.collectAsStateWithLifecycle()
    val isWatchlisted by quotesViewModel.isWatchlisted.collectAsStateWithLifecycle()

    QuotesScreen(
        symbol = symbol,
        profile = profile,
        timeSeries = timeSeries,
        signals = signals,
        signalSummary = signalSummary,
        isWatchlisted = isWatchlisted,
        onNavigateBack = onNavigateBack,
        onNavigateToQuote = onNavigateToQuote,
        onShowSnackbar = onShowSnackbar,
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
    profile: Result<Profile, DataError.Network>,
    timeSeries: Map<TimePeriod, Result<Map<String, HistoricalData>, DataError.Network>>,
    signals: Map<Interval, Result<Map<TechnicalIndicator, AnalysisSignal>, DataError.Network>>,
    signalSummary: Map<Interval, Result<Map<IndicatorType, AnalysisSignalSummary>, DataError.Network>>,
    isWatchlisted: Boolean,
    onNavigateBack: () -> Unit,
    onNavigateToQuote: (String) -> Unit,
    onShowSnackbar: suspend (String, String?, SnackbarDuration) -> Boolean,
    addToWatchlistLocal: (SimpleQuoteData) -> Unit,
    addToWatchListNetwork: () -> Unit,
    deleteFromWatchlist: () -> Unit,
    addToRecentQuotesLocal: (SimpleQuoteData) -> Unit,
    addToRecentQuotesNetwork: () -> Unit,
) {
    Scaffold(
        topBar = {
            val quoteData = when (profile) {
                is Result.Success -> SimpleQuoteData(
                    symbol = profile.data.quote.symbol,
                    name = profile.data.quote.name,
                    price = profile.data.quote.price,
                    change = profile.data.quote.change,
                    percentChange = profile.data.quote.percentChange,
                    logo = profile.data.quote.logo
                )

                else -> null
            }
            QuoteTopBar(
                onNavigateBack = onNavigateBack,
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
        when (profile) {
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
                        symbol = profile.data.quote.symbol,
                        name = profile.data.quote.name,
                        price = profile.data.quote.price,
                        change = profile.data.quote.change,
                        percentChange = profile.data.quote.percentChange,
                        logo = profile.data.quote.logo
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
                                name = profile.data.quote.name,
                                symbol = profile.data.quote.symbol,
                                price = profile.data.quote.price,
                                afterHoursPrice = profile.data.quote.afterHoursPrice,
                                change = profile.data.quote.change,
                                percentChange = profile.data.quote.percentChange,
                                logo = profile.data.quote.logo,
                            )
                        }

                        item {
                            QuoteChart(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 200.dp, max = 400.dp),
                                listState = listState,
                                onShowSnackbar = onShowSnackbar,
                                timeSeries = timeSeries,
                            )
                        }

                        profile.data.quote.ytdReturn?.let {
                            item {
                                QuotePerformance(
                                    symbol = profile.data.quote.symbol,
                                    ytdReturn = it,
                                    yearReturn = profile.data.quote.yearReturn,
                                    threeYearReturn = profile.data.quote.threeYearReturn,
                                    fiveYearReturn = profile.data.quote.fiveYearReturn,
                                    sector = profile.data.quote.sector,
                                    sectorPerformance = profile.data.performance
                                )
                            }
                        }

                        item {
                            SimilarQuoteFeed(
                                symbol = profile.data.quote.symbol,
                                similarQuotes = profile.data.similar,
                                onNavigateToQuote = onNavigateToQuote
                            )
                        }

                        item {
                            QuoteScreenPager(
                                quote = profile.data.quote,
                                news = profile.data.news,
                                signals = signals,
                                signalSummary = signalSummary,
                                onShowSnackbar = onShowSnackbar,
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
    pe = "12.34",
    eps = "1.23",
    beta = "1.23",
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
            timeSeries = mapOf(TimePeriod.YEAR_TO_DATE to Result.Success(emptyMap())),
            profile = Result.Success(
                Profile(
                    quote = previewFullQuoteData,
                    similar = emptyList(),
                    news = emptyList(),
                    performance = MarketSector(
                        sector = "Technology",
                        dayReturn = "+1.5%",
                        yearReturn = "+1.5%",
                        threeYearReturn = "+1.5%",
                        fiveYearReturn = "+1.5%",
                        ytdReturn = "+1.5%"
                    )
                )
            ),
            signals = mapOf(Interval.DAILY to Result.Success(emptyMap())),
            signalSummary = mapOf(Interval.DAILY to Result.Success(emptyMap())),
            isWatchlisted = false,
            onNavigateBack = {},
            onNavigateToQuote = {},
            onShowSnackbar = { _, _, _ -> true },
            addToWatchlistLocal = { },
            addToWatchListNetwork = { },
            deleteFromWatchlist = {},
            addToRecentQuotesLocal = { },
            addToRecentQuotesNetwork = { }
        )
    }
}



