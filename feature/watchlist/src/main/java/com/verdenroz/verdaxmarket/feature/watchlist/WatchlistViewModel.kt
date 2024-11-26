package com.verdenroz.verdaxmarket.feature.watchlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.verdenroz.verdaxmarket.core.common.error.DataError
import com.verdenroz.verdaxmarket.core.common.result.Result
import com.verdenroz.verdaxmarket.core.data.repository.WatchlistRepository
import com.verdenroz.verdaxmarket.core.domain.GetSubscribedWatchlistUseCase
import com.verdenroz.verdaxmarket.core.model.SimpleQuoteData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WatchlistViewModel @Inject constructor(
    private val watchlistRepository: WatchlistRepository,
    getSubscribedWatchlistUseCase: GetSubscribedWatchlistUseCase
) : ViewModel() {

    private val localWatchlist: StateFlow<Result<List<SimpleQuoteData>, DataError.Network>> =
        watchlistRepository.watchlist
            .map { quotes -> Result.Success(quotes) }
            .stateIn(
                viewModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = Result.Loading()
            )

    // Displayed watchlist that combines local changes with live socket updates
    val liveWatchlist: StateFlow<Result<List<SimpleQuoteData>, DataError.Network>> =
        localWatchlist
            .flatMapLatest { result ->
                when (result) {
                    is Result.Success -> {
                        val symbols = result.data.map { it.symbol }
                        when {
                            // User cleared the list - show empty success state
                            symbols.isEmpty() -> flowOf(Result.Success(emptyList()))
                            // Normal case - get live updates
                            else -> getSubscribedWatchlistUseCase(symbols)
                        }
                    }
                    else -> flowOf(result)
                }
            }
            .stateIn(
                viewModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = Result.Loading()
            )

    val symbols: StateFlow<List<String>> = watchlistRepository.watchlist.map { quotes ->
        quotes.map { it.symbol }
    }.stateIn(
        viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
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