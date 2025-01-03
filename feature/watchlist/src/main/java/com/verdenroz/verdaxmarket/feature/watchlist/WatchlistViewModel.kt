package com.verdenroz.verdaxmarket.feature.watchlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.verdenroz.verdaxmarket.core.common.error.DataError
import com.verdenroz.verdaxmarket.core.common.result.Result
import com.verdenroz.verdaxmarket.core.data.repository.WatchlistRepository
import com.verdenroz.verdaxmarket.core.model.WatchlistQuote
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface WatchlistState {
    object Loading : WatchlistState
    data class Success(val data: Map<String, WatchlistQuote>) : WatchlistState
    data class Error(val error: DataError) : WatchlistState
}

@HiltViewModel
class WatchlistViewModel @Inject constructor(
    private val watchlistRepository: WatchlistRepository,
) : ViewModel() {

    val watchlistState: StateFlow<WatchlistState> =
        // Sync watchlist with quotes from socket
        combine(
            watchlistRepository.watchlist,
            watchlistRepository.quotes
        ) { dbQuotes, liveResult ->
            when (liveResult) {
                is Result.Success -> {
                    // Create a map of all quotes, preferring live data when available
                    val quotesMap = buildMap<String, WatchlistQuote> {
                        liveResult.data.forEach { liveQuote ->
                            put(liveQuote.symbol, liveQuote)
                        }
                        dbQuotes.forEach { dbQuote ->
                            putIfAbsent(dbQuote.symbol, dbQuote)
                        }
                    }
                    WatchlistState.Success(quotesMap)
                }
                is Result.Error -> WatchlistState.Error(liveResult.error)
                is Result.Loading -> WatchlistState.Loading
            }
        }.stateIn(
            viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = WatchlistState.Loading
        )

    val watchlist: StateFlow<List<WatchlistQuote>> =
        watchlistRepository.watchlist.map { it.sortedBy { it.order } }
            .stateIn(
                viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = emptyList()
            )

    fun deleteFromWatchlist(symbol: String) {
        viewModelScope.launch {
            watchlistRepository.deleteFromWatchList(symbol)
        }
    }

    fun updateWatchlist(watchlist: List<WatchlistQuote>) {
        viewModelScope.launch {
            watchlistRepository.updateWatchlist(watchlist)
        }
    }

    fun clearWatchlist() {
        viewModelScope.launch {
            watchlistRepository.clearWatchList()
        }
    }

    fun moveUp(symbol: String) {
        viewModelScope.launch {
            watchlistRepository.moveUp(symbol)
        }
    }

    fun moveDown(symbol: String) {
        viewModelScope.launch {
            watchlistRepository.moveDown(symbol)
        }
    }
}