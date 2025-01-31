package com.verdenroz.verdaxmarket.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.verdenroz.verdaxmarket.core.common.enums.Interval
import com.verdenroz.verdaxmarket.core.common.enums.TimePeriod
import com.verdenroz.verdaxmarket.core.common.error.DataError
import com.verdenroz.verdaxmarket.core.common.result.Result
import com.verdenroz.verdaxmarket.core.data.repository.MarketInfoRepository
import com.verdenroz.verdaxmarket.core.data.repository.QuoteRepository
import com.verdenroz.verdaxmarket.core.data.repository.UserDataRepository
import com.verdenroz.verdaxmarket.core.model.HistoricalData
import com.verdenroz.verdaxmarket.core.model.MarketIndex
import com.verdenroz.verdaxmarket.core.model.MarketMover
import com.verdenroz.verdaxmarket.core.model.MarketSector
import com.verdenroz.verdaxmarket.core.model.News
import com.verdenroz.verdaxmarket.core.model.enums.Sector
import com.verdenroz.verdaxmarket.core.model.filterByRegion
import com.verdenroz.verdaxmarket.core.model.toMarketIndexName
import com.verdenroz.verdaxmarket.core.model.enums.toSymbol
import com.verdenroz.verdaxmarket.core.model.enums.toSymbols
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.toList
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    marketInfoRepository: MarketInfoRepository,
    userDataRepository: UserDataRepository,
    private val quoteRepository: QuoteRepository,
) : ViewModel() {

    private fun <T> stateFlowFromRepository(
        repositoryFlow: Flow<Result<T, DataError.Network>>
    ): StateFlow<Result<T, DataError.Network>> {
        return repositoryFlow
            .distinctUntilChanged()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                Result.Loading(true)
            )
    }

    val headlines: StateFlow<Result<List<News>, DataError.Network>> =
        stateFlowFromRepository(marketInfoRepository.headlines)

    val indices: StateFlow<Result<List<MarketIndex>, DataError.Network>> = combine(
        marketInfoRepository.indices,
        userDataRepository.userSetting
    ) { indicesResult, userSetting ->
        when (indicesResult) {
            is Result.Success -> Result.Success(
                indicesResult.data.filterByRegion(userSetting.regionPreference)
            )
            is Result.Loading -> Result.Loading(indicesResult.isLoading)
            is Result.Error -> Result.Error(indicesResult.error)
        }
    }.distinctUntilChanged().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        Result.Loading(true)
    )

    val indexTimeSeries: StateFlow<Map<String, Result<Map<String, HistoricalData>, DataError.Network>>> =
        userDataRepository.userSetting.flatMapLatest { userSetting ->
            val symbols = userSetting.regionPreference.toSymbols()
            combine(
                symbols.map { symbol ->
                    quoteRepository.getTimeSeries(
                        symbol = symbol,
                        timePeriod = TimePeriod.ONE_DAY,
                        interval = Interval.FIFTEEN_MINUTE
                    ).map { result -> symbol.toMarketIndexName() to result }
                }
            ) { results -> results.toMap() }
        }.distinctUntilChanged().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyMap()
        )

    val sectors: StateFlow<Result<List<MarketSector>, DataError.Network>> =
        stateFlowFromRepository(marketInfoRepository.sectors)

    val sectorTimeSeries: StateFlow<Map<Sector, Result<Map<String, HistoricalData>, DataError.Network>>> =
        flow {
            val results = Sector.entries.asFlow()
                .flatMapMerge(concurrency = Sector.entries.size) { sector ->
                    val symbol = sector.toSymbol()
                    quoteRepository.getTimeSeries(
                        symbol = symbol,
                        timePeriod = TimePeriod.ONE_YEAR,
                        interval = Interval.DAILY
                    ).map { result -> sector to result }
                }
                .toList()
                .toMap()
            emit(results)
        }.distinctUntilChanged().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyMap()
        )

    val actives: StateFlow<Result<List<MarketMover>, DataError.Network>> =
        stateFlowFromRepository(marketInfoRepository.actives)

    val losers: StateFlow<Result<List<MarketMover>, DataError.Network>> =
        stateFlowFromRepository(marketInfoRepository.losers)

    val gainers: StateFlow<Result<List<MarketMover>, DataError.Network>> =
        stateFlowFromRepository(marketInfoRepository.gainers)
}