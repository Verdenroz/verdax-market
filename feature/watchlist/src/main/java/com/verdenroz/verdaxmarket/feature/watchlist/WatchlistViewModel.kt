package com.verdenroz.verdaxmarket.feature.watchlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.verdenroz.verdaxmarket.core.common.error.DataError
import com.verdenroz.verdaxmarket.core.common.result.Result
import com.verdenroz.verdaxmarket.core.data.repository.WatchlistRepository
import com.verdenroz.verdaxmarket.core.model.SimpleQuoteData
import com.verdenroz.verdaxmarket.domain.GetSubscribedWatchlistUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WatchlistViewModel @Inject constructor(
    private val watchlistRepository: WatchlistRepository,
    getSubscribedWatchlistUseCase: GetSubscribedWatchlistUseCase
): ViewModel() {

    val watchlist: StateFlow<Result<List<SimpleQuoteData>, DataError.Network>> = getSubscribedWatchlistUseCase().stateIn(
        viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = Result.Loading()
    )

    fun addToWatchlist(quote: SimpleQuoteData) {
        viewModelScope.launch {
            watchlistRepository.addToWatchList(quote)
        }
    }

    fun deleteFromWatchlist(symbol: String) {
        viewModelScope.launch {
            watchlistRepository.deleteFromWatchList(symbol)
        }
    }

    fun clearWatchlist() {
        viewModelScope.launch {
            watchlistRepository.clearWatchList()
        }
    }
}