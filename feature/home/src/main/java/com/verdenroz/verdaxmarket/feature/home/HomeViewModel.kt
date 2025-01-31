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
import com.verdenroz.verdaxmarket.core.model.enums.TimePeriodPreference
import com.verdenroz.verdaxmarket.core.model.enums.toSymbol
import com.verdenroz.verdaxmarket.core.model.enums.toSymbols
import com.verdenroz.verdaxmarket.core.model.filterByRegion
import com.verdenroz.verdaxmarket.core.model.toMarketIndexName
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    marketInfoRepository: MarketInfoRepository,
    private val userDataRepository: UserDataRepository,
    private val quoteRepository: QuoteRepository,
) : ViewModel() {

    val sectors: StateFlow<Result<List<MarketSector>, DataError.Network>> =
        stateFlowFromRepository(marketInfoRepository.sectors)

    val headlines: StateFlow<Result<List<News>, DataError.Network>> =
        stateFlowFromRepository(marketInfoRepository.headlines)

    val actives: StateFlow<Result<List<MarketMover>, DataError.Network>> =
        stateFlowFromRepository(marketInfoRepository.actives)

    val losers: StateFlow<Result<List<MarketMover>, DataError.Network>> =
        stateFlowFromRepository(marketInfoRepository.losers)

    val gainers: StateFlow<Result<List<MarketMover>, DataError.Network>> =
        stateFlowFromRepository(marketInfoRepository.gainers)

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
                        timePeriod = userSetting.indexTimePeriodPreference.toTimePeriod(),
                        interval = userSetting.indexTimePeriodPreference.toInterval()
                    ).map { result -> symbol.toMarketIndexName() to result }
                }
            ) { results -> results.toMap() }
        }.distinctUntilChanged().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyMap()
        )

    val sectorTimeSeries: StateFlow<Map<Sector, Result<Map<String, HistoricalData>, DataError.Network>>> =
        userDataRepository.userSetting.flatMapLatest { userSetting ->
            combine(
                Sector.entries.map { sector ->
                    val symbol = sector.toSymbol()
                    quoteRepository.getTimeSeries(
                        symbol = symbol,
                        timePeriod = userSetting.sectorTimePeriodPreference.toTimePeriod(),
                        interval = userSetting.sectorTimePeriodPreference.toInterval()
                    ).map { result -> sector to result }
                }
            ) { results -> results.toMap() }
        }.distinctUntilChanged()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyMap()
            )

    val indexTimePeriodPreference: StateFlow<TimePeriodPreference> = userDataRepository.userSetting.map {
        it.indexTimePeriodPreference
    }.distinctUntilChanged().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        TimePeriodPreference.ONE_DAY
    )

    val sectorTimePeriodPreference: StateFlow<TimePeriodPreference> = userDataRepository.userSetting.map {
        it.sectorTimePeriodPreference
    }.distinctUntilChanged().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        TimePeriodPreference.ONE_DAY
    )

    fun updateIndexTimePeriod(timePeriod: TimePeriodPreference) {
        viewModelScope.launch {
            userDataRepository.setIndexTimePeriodPreference(timePeriod)
        }
    }

    fun updateSectorTimePeriod(timePeriod: TimePeriodPreference) {
        viewModelScope.launch {
            userDataRepository.setSectorTimePeriodPreference(timePeriod)
        }
    }

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

    private fun TimePeriodPreference.toTimePeriod(): TimePeriod {
        return when (this) {
            TimePeriodPreference.ONE_DAY -> TimePeriod.ONE_DAY
            TimePeriodPreference.FIVE_DAY -> TimePeriod.FIVE_DAY
            TimePeriodPreference.ONE_MONTH -> TimePeriod.ONE_MONTH
            TimePeriodPreference.SIX_MONTH -> TimePeriod.SIX_MONTH
            TimePeriodPreference.YEAR_TO_DATE -> TimePeriod.YEAR_TO_DATE
            TimePeriodPreference.ONE_YEAR -> TimePeriod.ONE_YEAR
            TimePeriodPreference.FIVE_YEAR -> TimePeriod.FIVE_YEAR
        }
    }

    private fun TimePeriodPreference.toInterval(): Interval {
        return when (this) {
            TimePeriodPreference.ONE_DAY -> Interval.FIFTEEN_MINUTE
            TimePeriodPreference.FIVE_DAY -> Interval.THIRTY_MINUTE
            else -> Interval.DAILY
        }
    }
}