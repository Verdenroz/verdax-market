package com.verdenroz.verdaxmarket.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.verdenroz.verdaxmarket.core.common.error.DataError
import com.verdenroz.verdaxmarket.core.common.result.Result
import com.verdenroz.verdaxmarket.core.data.repository.MarketInfoRepository
import com.verdenroz.verdaxmarket.core.model.MarketIndex
import com.verdenroz.verdaxmarket.core.model.MarketMover
import com.verdenroz.verdaxmarket.core.model.MarketSector
import com.verdenroz.verdaxmarket.core.model.News
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    marketInfoRepository: MarketInfoRepository,
) : ViewModel() {

    val headlines: StateFlow<Result<List<News>, DataError.Network>> =
        marketInfoRepository.headlines.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            Result.Loading(true)
        )

    val indices: StateFlow<Result<List<MarketIndex>, DataError.Network>> =
        marketInfoRepository.indices.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            Result.Loading(true)
        )

    val sectors: StateFlow<Result<List<MarketSector>, DataError.Network>> =
        marketInfoRepository.sectors.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            Result.Loading(true)
        )

    val actives: StateFlow<Result<List<MarketMover>, DataError.Network>> =
        marketInfoRepository.actives.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            Result.Loading(true)
        )

    val losers: StateFlow<Result<List<MarketMover>, DataError.Network>> =
        marketInfoRepository.losers.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            Result.Loading(true)
        )

    val gainers: StateFlow<Result<List<MarketMover>, DataError.Network>> =
        marketInfoRepository.gainers.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            Result.Loading(true)
        )

    fun refresh() {
        TODO()
    }
}

sealed interface HomeUiState {
    data object Refreshing : HomeUiState
    data object Success : HomeUiState
}