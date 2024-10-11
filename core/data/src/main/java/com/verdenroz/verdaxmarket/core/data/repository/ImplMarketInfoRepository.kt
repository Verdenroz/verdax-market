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
import com.verdenroz.verdaxmarket.core.data.utils.MarketStatusMonitor
import com.verdenroz.verdaxmarket.core.data.utils.handleNetworkException
import com.verdenroz.verdaxmarket.core.model.MarketIndex
import com.verdenroz.verdaxmarket.core.model.MarketInfo
import com.verdenroz.verdaxmarket.core.model.MarketMover
import com.verdenroz.verdaxmarket.core.model.MarketSector
import com.verdenroz.verdaxmarket.core.model.News
import com.verdenroz.verdaxmarket.core.network.FinanceQueryDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImplMarketInfoRepository @Inject constructor(
    private val api: FinanceQueryDataSource,
    socket: SocketRepository,
    marketStatusMonitor: MarketStatusMonitor,
    @Dispatcher(FinanceQueryDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) : MarketInfoRepository {

    /**
     * Market data flow that emits the latest market data from the websocket
     * If cannot connect or there is an error, it will fallback to polling the API
     */
    private val market: StateFlow<Result<MarketInfo, DataError.Network>> = socket.market.stateIn(
        CoroutineScope(ioDispatcher),
        SharingStarted.Lazily,
        Result.Loading()
    )

    override val isOpen: Flow<Boolean> = marketStatusMonitor.isMarketOpen.stateIn(
        CoroutineScope(ioDispatcher),
        SharingStarted.WhileSubscribed(5000L),
        marketStatusMonitor.isMarketOpen()
    )

    override val indices: Flow<Result<List<MarketIndex>, DataError.Network>> =
        market.flatMapConcat { marketInfo ->
            when (marketInfo) {
                is Result.Success -> flowOf(Result.Success(marketInfo.data.indices))
                is Result.Loading -> flowOf(Result.Loading())
                else -> isOpen.flatMapMerge { isOpen ->
                    flow {
                        while (true) {
                            val indices = api.getIndexes().asExternalModel()
                            emit(Result.Success(indices))
                            when (isOpen) {
                                true -> delay(MARKET_DATA_REFRESH_OPEN)
                                false -> delay(MARKET_DATA_REFRESH_CLOSED)
                            }
                        }
                    }
                }
            }
        }.flowOn(ioDispatcher).catch { e -> handleNetworkException(e) }

    override val actives: Flow<Result<List<MarketMover>, DataError.Network>> =
        market.flatMapConcat { marketInfo ->
            when (marketInfo) {
                is Result.Success -> flowOf(Result.Success(marketInfo.data.actives))
                is Result.Loading -> flowOf(Result.Loading())
                else -> isOpen.flatMapMerge { isOpen ->
                    flow {
                        while (true) {
                            val actives = api.getActives().asExternalModel()
                            emit(Result.Success(actives))
                            when (isOpen) {
                                true -> delay(MARKET_DATA_REFRESH_OPEN)
                                false -> delay(MARKET_DATA_REFRESH_CLOSED)
                            }
                        }
                    }
                }
            }
        }.flowOn(ioDispatcher).catch { e -> handleNetworkException(e) }

    override val losers: Flow<Result<List<MarketMover>, DataError.Network>> =
        market.flatMapConcat { marketInfo ->
            when (marketInfo) {
                is Result.Success -> flowOf(Result.Success(marketInfo.data.losers))
                is Result.Loading -> flowOf(Result.Loading())
                else -> isOpen.flatMapMerge { isOpen ->
                    flow {
                        while (true) {
                            val losers = api.getLosers().asExternalModel()
                            emit(Result.Success(losers))
                            when (isOpen) {
                                true -> delay(MARKET_DATA_REFRESH_OPEN)
                                false -> delay(MARKET_DATA_REFRESH_CLOSED)
                            }
                        }
                    }
                }
            }
        }.flowOn(ioDispatcher).catch { e -> handleNetworkException(e) }

    override val gainers: Flow<Result<List<MarketMover>, DataError.Network>> =
        market.flatMapConcat { marketInfo ->
            when (marketInfo) {
                is Result.Success -> flowOf(Result.Success(marketInfo.data.gainers))
                is Result.Loading -> flowOf(Result.Loading())
                else -> isOpen.flatMapMerge { isOpen ->
                    flow {
                        while (true) {
                            val gainers = api.getGainers().asExternalModel()
                            emit(Result.Success(gainers))
                            when (isOpen) {
                                true -> delay(MARKET_DATA_REFRESH_OPEN)
                                false -> delay(MARKET_DATA_REFRESH_CLOSED)
                            }
                        }
                    }
                }
            }
        }.flowOn(ioDispatcher).catch { e -> handleNetworkException(e) }

    override val headlines: Flow<Result<List<News>, DataError.Network>> =
        market.flatMapConcat { marketInfo ->
            when (marketInfo) {
                is Result.Success -> flowOf(Result.Success(marketInfo.data.headlines))
                is Result.Loading -> flowOf(Result.Loading())
                else -> isOpen.flatMapMerge { isOpen ->
                    flow {
                        while (true) {
                            val headlines = api.getNews().asExternalModel()
                            emit(Result.Success(headlines))
                            when (isOpen) {
                                true -> delay(SLOW_REFRESH_INTERVAL_OPEN)
                                false -> delay(SLOW_REFRESH_INTERVAL_CLOSED)
                            }
                        }
                    }
                }
            }
        }.flowOn(ioDispatcher).catch { e -> handleNetworkException(e) }

    override val sectors: Flow<Result<List<MarketSector>, DataError.Network>> =
        market.flatMapConcat { marketInfo ->
            when (marketInfo) {
                is Result.Success -> flowOf(Result.Success(marketInfo.data.sectors))
                is Result.Loading -> flowOf(Result.Loading())
                else -> isOpen.flatMapMerge { isOpen ->
                    flow {
                        while (true) {
                            val sectors = api.getSectors().asExternalModel()
                            emit(Result.Success(sectors))
                            when (isOpen) {
                                true -> delay(SLOW_REFRESH_INTERVAL_OPEN)
                                false -> delay(NEVER_REFRESH_INTERVAL)
                            }
                        }
                    }
                }
            }
        }.flowOn(ioDispatcher).catch { e -> handleNetworkException(e) }
}