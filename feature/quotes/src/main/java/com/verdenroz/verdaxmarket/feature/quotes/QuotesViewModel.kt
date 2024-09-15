package com.verdenroz.verdaxmarket.feature.quotes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.verdenroz.verdaxmarket.core.common.enums.Interval
import com.verdenroz.verdaxmarket.core.common.enums.TimePeriod
import com.verdenroz.verdaxmarket.core.common.error.DataError
import com.verdenroz.verdaxmarket.core.common.result.Result
import com.verdenroz.verdaxmarket.core.data.repository.QuoteRepository
import com.verdenroz.verdaxmarket.core.data.repository.RecentSearchRepository
import com.verdenroz.verdaxmarket.core.data.repository.WatchlistRepository
import com.verdenroz.verdaxmarket.core.model.AnalysisSignal
import com.verdenroz.verdaxmarket.core.model.AnalysisSignalSummary
import com.verdenroz.verdaxmarket.core.model.FullQuoteData
import com.verdenroz.verdaxmarket.core.model.HistoricalData
import com.verdenroz.verdaxmarket.core.model.MarketSector
import com.verdenroz.verdaxmarket.core.model.News
import com.verdenroz.verdaxmarket.core.model.Profile
import com.verdenroz.verdaxmarket.core.model.SimpleQuoteData
import com.verdenroz.verdaxmarket.core.model.indicators.IndicatorType
import com.verdenroz.verdaxmarket.core.model.indicators.TechnicalIndicator
import com.verdenroz.verdaxmarket.domain.GetAnalysisSignalSummaryUseCase
import com.verdenroz.verdaxmarket.domain.GetAnalysisSignalsUseCase
import com.verdenroz.verdaxmarket.domain.GetSubscribedProfileUseCase
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
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = QuotesViewModel.QuotesViewModelFactory::class)
class QuotesViewModel @AssistedInject constructor(
    @Assisted private val symbol: String,
    quotesRepository: QuoteRepository,
    private val recentSearchRepository: RecentSearchRepository,
    private val watchlistRepository: WatchlistRepository,
    getSubscribedQuoteUseCase: GetSubscribedQuoteUseCase,
    getSubscribedProfileUseCase: GetSubscribedProfileUseCase,
    getTimeSeriesMapUseCase: GetTimeSeriesMapUseCase,
    getAnalysisSignalsUseCase: GetAnalysisSignalsUseCase,
    getAnalysisSignalSummaryUseCase: GetAnalysisSignalSummaryUseCase,
) : ViewModel() {

    @AssistedFactory
    interface QuotesViewModelFactory {
        fun create(symbol: String): QuotesViewModel
    }

    /**
     * The current subscribed [Profile] for the symbol with aggregated data
     * for the quote, similar, news, and sector performance
     * If profile is an error, data must be retrieved from the API instead
     */
    val profile: StateFlow<Result<Profile, DataError.Network>> =
        getSubscribedProfileUseCase(symbol).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            Result.Loading(true)
        )

    /**
     *  The current subscribed quote for the symbol
     */
    val quote: StateFlow<Result<FullQuoteData, DataError.Network>> =
        profile.flatMapLatest { profile ->
            when (profile) {
                is Result.Success -> flowOf(Result.Success(profile.data.quote))
                else -> getSubscribedQuoteUseCase(symbol)
            }
        }.stateIn(
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
        profile.flatMapLatest { profile ->
            when (profile) {
                is Result.Success -> flowOf(Result.Success(profile.data.similar))
                else -> quotesRepository.getSimilarStocks(symbol)
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            Result.Loading(true)
        )

    /**
     * The [MarketSector] performance of the current symbol if available
     */
    val sectorPerformance: StateFlow<Result<MarketSector?, DataError.Network>> =
        profile.flatMapLatest { profile ->
            when (profile) {
                is Result.Success -> flowOf(Result.Success(profile.data.performance))
                else -> quotesRepository.getSectorBySymbol(symbol)
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            Result.Loading(true)
        )

    /**
     * The list of [News] for the current symbol
     */
    val news: StateFlow<Result<List<News>, DataError.Network>> =
        profile.flatMapLatest { profile ->
            when (profile) {
                is Result.Success -> flowOf(Result.Success(profile.data.news))
                else -> quotesRepository.getNewsForSymbol(symbol)
            }
        }.stateIn(
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


    fun addToWatchListLocal(quote: SimpleQuoteData) {
        viewModelScope.launch {
            val result = watchlistRepository.addToWatchList(quote)
            if (result is Result.Success) {
                _isWatchlisted.value = true
            }
        }
    }

    fun addToWatchlistNetwork() {
        viewModelScope.launch {
            val result = watchlistRepository.addToWatchList(symbol)
            if (result is Result.Success) {
                _isWatchlisted.value = true
            }
        }
    }

    fun deleteFromWatchlist() {
        viewModelScope.launch {
            val result = watchlistRepository.deleteFromWatchList(symbol)
            if (result is Result.Success) {
                _isWatchlisted.value = false
            }
        }
    }

    fun addToRecentQuotesLocal(quote: SimpleQuoteData) {
        viewModelScope.launch {
            recentSearchRepository.upsertRecentQuote(quote)
        }
    }

    fun addToRecentQuotesNetwork() {
        viewModelScope.launch {
            recentSearchRepository.upsertRecentQuote(symbol)
        }
    }

}