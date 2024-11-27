package com.verdenroz.verdaxmarket

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.verdenroz.verdaxmarket.core.data.repository.UserDataRepository
import com.verdenroz.verdaxmarket.core.data.repository.WatchlistRepository
import com.verdenroz.verdaxmarket.core.model.UserSetting
import com.verdenroz.verdaxmarket.core.network.FinanceQueryDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    userDataRepository: UserDataRepository,
    watchlistRepository: WatchlistRepository,
    api: FinanceQueryDataSource
) : ViewModel() {

    init {
        viewModelScope.launch {
            // Start fetching data from the API to cache symbols
            val symbols = watchlistRepository.getWatchlist().map { it.symbol }
            api.getBulkQuote(symbols)
        }
    }

    val uiState: StateFlow<MainActivityUiState> = userDataRepository.userSetting.map {
        MainActivityUiState.Success(it)
    }.stateIn(
        scope = viewModelScope,
        initialValue = MainActivityUiState.Loading,
        started = SharingStarted.WhileSubscribed(5000L),
    )
}

sealed interface MainActivityUiState {
    data object Loading : MainActivityUiState
    data class Success(val userSetting: UserSetting) : MainActivityUiState
}