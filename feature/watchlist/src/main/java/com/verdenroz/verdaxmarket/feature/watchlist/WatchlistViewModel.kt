package com.verdenroz.verdaxmarket.feature.watchlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.verdenroz.verdaxmarket.core.common.error.DataError
import com.verdenroz.verdaxmarket.core.common.result.Result
import com.verdenroz.verdaxmarket.core.data.repository.WatchlistRepository
import com.verdenroz.verdaxmarket.core.model.SimpleQuoteData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface WatchlistState {
    object Loading : WatchlistState
    data class Success(val data: Map<String, SimpleQuoteData>) : WatchlistState
    data class Error(val error: DataError) : WatchlistState
}

@HiltViewModel
class WatchlistViewModel @Inject constructor(
    private val watchlistRepository: WatchlistRepository,
) : ViewModel() {

    val watchlistState: StateFlow<WatchlistState> =
        watchlistRepository.quotes.map { liveResult ->
            when (liveResult) {
                is Result.Success -> WatchlistState.Success(liveResult.data.associateBy { it.symbol })
                is Result.Error -> WatchlistState.Error(liveResult.error)
                is Result.Loading -> WatchlistState.Loading
            }
        }.stateIn(
            viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = WatchlistState.Loading
        )

    val symbols: StateFlow<List<String>> = watchlistRepository.watchlist.map { quotes ->
        quotes.map { it.symbol }
    }.stateIn(
        viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

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