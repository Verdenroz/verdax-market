package com.verdenroz.verdaxmarket.feature.quotes.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.verdenroz.verdaxmarket.core.common.enums.Interval
import com.verdenroz.verdaxmarket.core.common.error.DataError
import com.verdenroz.verdaxmarket.core.common.result.Result
import com.verdenroz.verdaxmarket.core.designsystem.components.VxmTabRowPager
import com.verdenroz.verdaxmarket.core.designsystem.theme.ThemePreviews
import com.verdenroz.verdaxmarket.core.designsystem.theme.VxmTheme
import com.verdenroz.verdaxmarket.core.model.AnalysisSignal
import com.verdenroz.verdaxmarket.core.model.AnalysisSignalSummary
import com.verdenroz.verdaxmarket.core.model.FullQuoteData
import com.verdenroz.verdaxmarket.core.model.News
import com.verdenroz.verdaxmarket.core.model.indicators.IndicatorType
import com.verdenroz.verdaxmarket.core.model.indicators.TechnicalIndicator
import com.verdenroz.verdaxmarket.feature.quotes.R
import com.verdenroz.verdaxmarket.feature.quotes.components.analysis.QuoteAnalysis
import com.verdenroz.verdaxmarket.feature.quotes.previewFullQuoteData
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun QuoteScreenPager(
    quote: FullQuoteData,
    news: List<News>,
    signals: Map<Interval, Result<Map<TechnicalIndicator, AnalysisSignal>, DataError.Network>>,
    signalSummary: Map<Interval, Result<Map<IndicatorType, AnalysisSignalSummary>, DataError.Network>>,
    isHintsEnabled: Boolean,
) {
    val state = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()
    var interval by rememberSaveable { mutableStateOf(Interval.DAILY) }

    Scaffold(
        modifier = Modifier
            .requiredHeightIn(
                min = 300.dp,
                max = if (
                    (news.size < 5 && state.currentPage == 1) ||
                    (signals[interval] is Result.Error && state.currentPage == 2) ||
                    (signals[interval] is Result.Success && state.currentPage == 2 && (signals[interval] as Result.Success).data.isEmpty())
                ) 300.dp else 900.dp
            )
            .fillMaxWidth(),
        topBar = {
            val tabTitles = listOf(
                stringResource(id = R.string.feature_quotes_summary),
                stringResource(id = R.string.feature_quotes_news),
                stringResource(id = R.string.feature_quotes_analysis)
            )
            VxmTabRowPager(state = state) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        text = { Text(title) },
                        selected = state.currentPage == index,
                        onClick = {
                            scope.launch {
                                state.animateScrollToPage(index)
                            }
                        }
                    )
                }
            }
        }
    ) { padding ->
        HorizontalPager(
            state = state,
            modifier = Modifier
                .padding(padding)
        ) { page ->
            when (page) {
                0 -> {
                    QuoteSummary(
                        quote = quote,
                        isHintsEnabled = isHintsEnabled
                    )
                }

                1 -> {
                    QuoteNewsFeed(
                        news = news
                    )
                }

                2 -> {
                    QuoteAnalysis(
                        isHintsEnabled = isHintsEnabled,
                        interval = interval,
                        signals = signals,
                        signalSummary = signalSummary,
                        updateInterval = { interval = it },
                    )
                }
            }
        }
    }
}

@ThemePreviews
@Composable
private fun PreviewQuoteScreenPager() {
    VxmTheme {
        QuoteScreenPager(
            quote = previewFullQuoteData,
            news = emptyList(),
            signals = mapOf(
                Interval.DAILY to Result.Success(emptyMap()),
                Interval.WEEKLY to Result.Success(emptyMap()),
                Interval.MONTHLY to Result.Success(emptyMap())
            ),
            signalSummary = mapOf(
                Interval.DAILY to Result.Success(emptyMap()),
                Interval.WEEKLY to Result.Success(emptyMap()),
                Interval.MONTHLY to Result.Success(emptyMap())
            ),
            isHintsEnabled = true,
        )
    }
}