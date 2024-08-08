package com.verdenroz.verdaxmarket.feature.quotes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.verdenroz.verdaxmarket.core.common.enums.Interval
import com.verdenroz.verdaxmarket.core.common.enums.TimePeriod
import com.verdenroz.verdaxmarket.core.common.error.DataError
import com.verdenroz.verdaxmarket.core.common.result.Result
import com.verdenroz.verdaxmarket.core.data.repository.QuoteRepository
import com.verdenroz.verdaxmarket.core.data.repository.WatchlistRepository
import com.verdenroz.verdaxmarket.core.model.AnalysisSignal
import com.verdenroz.verdaxmarket.core.model.AnalysisSignalSummary
import com.verdenroz.verdaxmarket.core.model.FullQuoteData
import com.verdenroz.verdaxmarket.core.model.HistoricalData
import com.verdenroz.verdaxmarket.core.model.MarketSector
import com.verdenroz.verdaxmarket.core.model.News
import com.verdenroz.verdaxmarket.core.model.SimpleQuoteData
import com.verdenroz.verdaxmarket.core.model.indicators.IndicatorType
import com.verdenroz.verdaxmarket.core.model.indicators.TechnicalIndicator
import com.verdenroz.verdaxmarket.domain.GetAnalysisSignalSummaryUseCase
import com.verdenroz.verdaxmarket.domain.GetAnalysisSignalsUseCase
import com.verdenroz.verdaxmarket.domain.GetSubscribedQuoteUseCase
import com.verdenroz.verdaxmarket.domain.GetTimeSeriesMapUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = QuotesViewModel.QuotesViewModelFactory::class)
class QuotesViewModel @AssistedInject constructor(
    @Assisted private val symbol: String,
    quotesRepository: QuoteRepository,
    private val watchlistRepository: WatchlistRepository,
    getSubscribedQuoteUseCase: GetSubscribedQuoteUseCase,
    getTimeSeriesMapUseCase: GetTimeSeriesMapUseCase,
    getAnalysisSignalsUseCase: GetAnalysisSignalsUseCase,
    getAnalysisSignalSummaryUseCase: GetAnalysisSignalSummaryUseCase,
) : ViewModel() {

    @AssistedFactory
    interface QuotesViewModelFactory {
        fun create(symbol: String): QuotesViewModel
    }

    /**
     *  The current subscribed quote for the symbol
     */
    val quote: StateFlow<Result<FullQuoteData, DataError.Network>> =
        getSubscribedQuoteUseCase(symbol).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            Result.Loading(true)
        )

    /**
     * The complete time series data for each [TimePeriod]
     */
    val timeSeries: StateFlow<Map<TimePeriod, Result<Map<String, HistoricalData>, DataError.Network>>> =
        getTimeSeriesMapUseCase(symbol).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            emptyMap()
        )

    /**
     *  The similar quotes to the current symbol
     */
    val similarQuotes: StateFlow<Result<List<SimpleQuoteData>, DataError.Network>> =
        quotesRepository.getSimilarStocks(symbol)
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000L),
                Result.Loading(true)
            )

    /**
     * The [MarketSector] performance of the current symbol if available
     */
    val sectorPerformance: StateFlow<Result<MarketSector?, DataError.Network>> =
        quotesRepository.getSectorBySymbol(symbol)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), Result.Loading(true))

    /**
     * The list of [News] for the current symbol
     */
    val news: StateFlow<Result<List<News>, DataError.Network>> =
        quotesRepository.getNewsForSymbol(symbol)
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000L),
                Result.Loading(true)
            )

    /**
     * The complete analysis data for each [Interval] as a map of [TechnicalIndicator] to [AnalysisSignal]
     */
    val signals: StateFlow<Map<Interval, Result<Map<TechnicalIndicator, AnalysisSignal>, DataError.Network>>> =
        getAnalysisSignalsUseCase(symbol, quote).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            emptyMap()
        )


    /**
     * The summary of the [signals] for each [Interval]
     */
    val signalSummary: StateFlow<Map<Interval, Result<Map<IndicatorType, AnalysisSignalSummary>, DataError.Network>>> =
        getAnalysisSignalSummaryUseCase(signals).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            emptyMap()
        )

    private val _isWatchlisted = MutableStateFlow(false)
    val isWatchlisted: StateFlow<Boolean> = _isWatchlisted.asStateFlow()

    init {
        viewModelScope.launch {
            watchlistRepository.isSymbolInWatchlist(symbol).collect { isInWatchlist ->
                _isWatchlisted.value = isInWatchlist
            }
        }
    }

    fun addToWatchList() {
        viewModelScope.launch {
            val result = watchlistRepository.addToWatchList(symbol)
            if (result is Result.Success) {
                _isWatchlisted.value = true
            }

        }
    }

    fun deleteFromWatchList() {
        viewModelScope.launch {
            val result = watchlistRepository.deleteFromWatchList(symbol)
            if (result is Result.Success) {
                _isWatchlisted.value = false
            }
        }
    }

}