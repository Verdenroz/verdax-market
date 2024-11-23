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
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WatchlistViewModel @Inject constructor(
    private val watchlistRepository: WatchlistRepository,
    getSubscribedWatchlistUseCase: GetSubscribedWatchlistUseCase
) : ViewModel() {

    private val symbols: StateFlow<List<String>> =
        watchlistRepository.watchlist
            .stateIn(
                viewModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = emptyList()
            )

    val watchlist: StateFlow<Result<List<SimpleQuoteData>, DataError.Network>> =
        symbols.flatMapLatest { symbols ->
            flow {
                emit(Result.Loading())
                if (symbols.isEmpty()) {
                    emit(Result.Success(emptyList()))
                } else {
                    emitAll(getSubscribedWatchlistUseCase(symbols))
                }
            }
        }.stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = Result.Loading()
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