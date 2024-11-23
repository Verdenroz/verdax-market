package com.verdenroz.verdaxmarket.feature.quotes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.verdenroz.verdaxmarket.core.common.enums.Interval
import com.verdenroz.verdaxmarket.core.common.enums.TimePeriod
import com.verdenroz.verdaxmarket.core.common.error.DataError
import com.verdenroz.verdaxmarket.core.common.result.Result
import com.verdenroz.verdaxmarket.core.data.repository.RecentSearchRepository
import com.verdenroz.verdaxmarket.core.data.repository.UserDataRepository
import com.verdenroz.verdaxmarket.core.data.repository.WatchlistRepository
import com.verdenroz.verdaxmarket.core.domain.GetAnalysisSignalSummaryUseCase
import com.verdenroz.verdaxmarket.core.domain.GetAnalysisSignalsUseCase
import com.verdenroz.verdaxmarket.core.domain.GetSubscribedProfileUseCase
import com.verdenroz.verdaxmarket.core.domain.GetTimeSeriesMapUseCase
import com.verdenroz.verdaxmarket.core.model.AnalysisSignal
import com.verdenroz.verdaxmarket.core.model.AnalysisSignalSummary
import com.verdenroz.verdaxmarket.core.model.HistoricalData
import com.verdenroz.verdaxmarket.core.model.Profile
import com.verdenroz.verdaxmarket.core.model.SimpleQuoteData
import com.verdenroz.verdaxmarket.core.model.indicators.IndicatorType
import com.verdenroz.verdaxmarket.core.model.indicators.TechnicalIndicator
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = QuotesViewModel.QuotesViewModelFactory::class)
class QuotesViewModel @AssistedInject constructor(
    @Assisted private val symbol: String,
    private val recentSearchRepository: RecentSearchRepository,
    private val watchlistRepository: WatchlistRepository,
    userDataRepository: UserDataRepository,
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
            SharingStarted.WhileSubscribed(),
            Result.Loading(true)
        )

    val timeSeries: StateFlow<Map<TimePeriod, Result<Map<String, HistoricalData>, DataError.Network>>> =
        getTimeSeriesMapUseCase(symbol).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            emptyMap()
        )

    val signals: StateFlow<Map<Interval, Result<Map<TechnicalIndicator, AnalysisSignal>, DataError.Network>>> =
        getAnalysisSignalsUseCase(symbol, profile).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            emptyMap()
        )

    val signalSummary: StateFlow<Map<Interval, Result<Map<IndicatorType, AnalysisSignalSummary>, DataError.Network>>> =
        getAnalysisSignalSummaryUseCase(signals).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            emptyMap()
        )

    val isWatchlisted: StateFlow<Boolean> = watchlistRepository.isSymbolInWatchlist(symbol)
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            false
        )

    val isHintsEnabled: StateFlow<Boolean> = userDataRepository.userSetting
        .map { it.hintsEnabled }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            true
        )

    fun addToWatchListLocal(quote: SimpleQuoteData) {
        viewModelScope.launch {
           watchlistRepository.addToWatchList(quote)
        }
    }

    fun addToWatchlistNetwork() {
        viewModelScope.launch {
            watchlistRepository.addToWatchList(symbol)
        }
    }

    fun deleteFromWatchlist() {
        viewModelScope.launch {
            watchlistRepository.deleteFromWatchList(symbol)
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