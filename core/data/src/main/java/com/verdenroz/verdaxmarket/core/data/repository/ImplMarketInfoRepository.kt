package com.verdenroz.verdaxmarket.core.data.repository

import com.verdenroz.verdaxmarket.core.common.dispatchers.Dispatcher
import com.verdenroz.verdaxmarket.core.common.dispatchers.FinanceQueryDispatchers
import com.verdenroz.verdaxmarket.core.common.error.DataError
import com.verdenroz.verdaxmarket.core.common.result.Result
import com.verdenroz.verdaxmarket.core.data.model.asExternalModel
import com.verdenroz.verdaxmarket.core.data.repository.MarketInfoRepository.Companion.MARKET_DATA_REFRESH_CLOSED
import com.verdenroz.verdaxmarket.core.data.repository.MarketInfoRepository.Companion.MARKET_DATA_REFRESH_OPEN
import com.verdenroz.verdaxmarket.core.data.repository.MarketInfoRepository.Companion.NEVER_REFRESH_INTERVAL
import com.verdenroz.verdaxmarket.core.data.repository.MarketInfoRepository.Companion.SLOW_REFRESH_INTERVAL_CLOSED
import com.verdenroz.verdaxmarket.core.data.repository.MarketInfoRepository.Companion.SLOW_REFRESH_INTERVAL_OPEN
import com.verdenroz.verdaxmarket.core.data.utils.ExceptionHandler
import com.verdenroz.verdaxmarket.core.data.utils.MarketStatusMonitor
import com.verdenroz.verdaxmarket.core.data.utils.catchAndEmitError
import com.verdenroz.verdaxmarket.core.model.MarketInfo
import com.verdenroz.verdaxmarket.core.model.MarketMover
import com.verdenroz.verdaxmarket.core.model.MarketSector
import com.verdenroz.verdaxmarket.core.model.News
import com.verdenroz.verdaxmarket.core.model.enums.MarketStatus
import com.verdenroz.verdaxmarket.core.network.FinanceQueryDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImplMarketInfoRepository @Inject constructor(
    private val api: FinanceQueryDataSource,
    private val exceptionHandler: ExceptionHandler,
    socket: SocketRepository,
    marketStatusMonitor: MarketStatusMonitor,
    @Dispatcher(FinanceQueryDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) : MarketInfoRepository {

    /**
     * Market data flow that emits the latest market data from the websocket
     */
    private val market: StateFlow<Result<MarketInfo, DataError.Network>> = socket.market.stateIn(
        CoroutineScope(ioDispatcher),
        SharingStarted.Lazily,
        Result.Loading()
    )

    override val isOpen: Flow<Boolean> = marketStatusMonitor.marketHours.map {
        it.status == MarketStatus.OPEN
    }.stateIn(
        CoroutineScope(ioDispatcher),
        SharingStarted.WhileSubscribed(5000L),
        true
    )

    override val indices = market.toMarketDataFlow(
        dataSelector = { it.indices },
        poll = { api.getIndexes() },
        transform = { it.asExternalModel() },
        refreshIntervalOpen = MARKET_DATA_REFRESH_OPEN,
        refreshIntervalClosed = MARKET_DATA_REFRESH_CLOSED
    )

    override val actives: Flow<Result<List<MarketMover>, DataError.Network>> = market.toMarketDataFlow(
        dataSelector = { it.actives },
        poll = { api.getActives() },
        transform = { it.asExternalModel() },
        refreshIntervalOpen = MARKET_DATA_REFRESH_OPEN,
        refreshIntervalClosed = MARKET_DATA_REFRESH_CLOSED
    )

    override val losers: Flow<Result<List<MarketMover>, DataError.Network>> = market.toMarketDataFlow(
        dataSelector = { it.losers },
        poll = { api.getLosers() },
        transform = { it.asExternalModel() },
        refreshIntervalOpen = MARKET_DATA_REFRESH_OPEN,
        refreshIntervalClosed = MARKET_DATA_REFRESH_CLOSED
    )

    override val gainers: Flow<Result<List<MarketMover>, DataError.Network>> = market.toMarketDataFlow(
        dataSelector = { it.gainers },
        poll = { api.getGainers() },
        transform = { it.asExternalModel() },
        refreshIntervalOpen = MARKET_DATA_REFRESH_OPEN,
        refreshIntervalClosed = MARKET_DATA_REFRESH_CLOSED
    )

    override val headlines: Flow<Result<List<News>, DataError.Network>> = market.toMarketDataFlow(
        dataSelector = { it.headlines },
        poll = { api.getNews() },
        transform = { it.asExternalModel() },
        refreshIntervalOpen = SLOW_REFRESH_INTERVAL_OPEN,
        refreshIntervalClosed = SLOW_REFRESH_INTERVAL_CLOSED
    )

    override val sectors: Flow<Result<List<MarketSector>, DataError.Network>> = market.toMarketDataFlow(
        dataSelector = { it.sectors },
        poll = { api.getSectors() },
        transform = { it.asExternalModel() },
        refreshIntervalOpen = SLOW_REFRESH_INTERVAL_OPEN,
        refreshIntervalClosed = NEVER_REFRESH_INTERVAL
    )

    /**
     * Converts a [StateFlow] of [MarketInfo] to a [Flow] of [List] of [T] from [R]
     * The flow will emit the latest data from the websocket and fallback to polling the API
     *
     * @param T the type of the data to be emitted
     * @param R the response type from the network
     *
     * @param dataSelector a function to select the data from the [MarketInfo]
     * @param poll a suspend function to poll the API for the data if the websocket fails
     * @param transform a function to transform the response to its external model
     * @param refreshIntervalOpen the refresh interval when the market is open (used only on polling)
     * @param refreshIntervalClosed the refresh interval when the market is closed (used only on polling)
     */
    private fun <T, R> StateFlow<Result<MarketInfo, DataError.Network>>.toMarketDataFlow(
        dataSelector: (MarketInfo) -> List<T>,
        poll: suspend () -> List<R>,
        transform: (List<R>) -> List<T>,
        refreshIntervalOpen: Long,
        refreshIntervalClosed: Long
    ): Flow<Result<List<T>, DataError.Network>> = flatMapConcat { marketInfo ->
        when (marketInfo) {
            is Result.Success -> flowOf(Result.Success(dataSelector(marketInfo.data)))
            is Result.Loading -> flowOf(Result.Loading())
            else -> isOpen.flatMapMerge { isOpen ->
                flow {
                    while (true) {
                        val data = transform(poll())
                        emit(Result.Success(data))
                        when (isOpen) {
                            true -> delay(refreshIntervalOpen)
                            false -> delay(refreshIntervalClosed)
                        }
                    }
                }
            }
        }
    }.flowOn(ioDispatcher).catchAndEmitError(exceptionHandler)
}