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
import com.verdenroz.verdaxmarket.core.model.MarketMover
import com.verdenroz.verdaxmarket.core.model.MarketSector
import com.verdenroz.verdaxmarket.core.model.News
import com.verdenroz.verdaxmarket.core.network.FinanceQueryDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImplMarketInfoRepository @Inject constructor(
    private val api: FinanceQueryDataSource,
    marketStatusMonitor: MarketStatusMonitor,
    @Dispatcher(FinanceQueryDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) : MarketInfoRepository {

    override val isOpen: Flow<Boolean> = marketStatusMonitor.isMarketOpen.stateIn(
        CoroutineScope(ioDispatcher),
        SharingStarted.WhileSubscribed(5000L),
        marketStatusMonitor.isMarketOpen()
    )

    override val indices: Flow<Result<List<MarketIndex>, DataError.Network>> =
        isOpen.flatMapLatest { isOpen ->
            flow<Result<List<MarketIndex>, DataError.Network>> {
                while (true) {
                    val indexes = api.getIndexes().asExternalModel()
                    emit(Result.Success(indexes))

                    val refreshInterval =
                        if (isOpen) MARKET_DATA_REFRESH_OPEN else MARKET_DATA_REFRESH_CLOSED // 20 seconds or 10 minutes
                    delay(refreshInterval)
                }
            }
        }.flowOn(Dispatchers.IO).catch { e -> emit(handleNetworkException(e)) }

    override val actives: Flow<Result<List<MarketMover>, DataError.Network>> =
        isOpen.flatMapLatest { isOpen ->
            flow<Result<List<MarketMover>, DataError.Network>> {
                while (true) {
                    val quotes = api.getActives().asExternalModel()
                    emit(Result.Success(quotes))

                    val refreshInterval =
                        if (isOpen) MARKET_DATA_REFRESH_OPEN else MARKET_DATA_REFRESH_CLOSED // 20 seconds or 10 minutes
                    delay(refreshInterval)
                }
            }
        }.flowOn(Dispatchers.IO).catch { e -> emit(handleNetworkException(e)) }

    override val losers: Flow<Result<List<MarketMover>, DataError.Network>> =
        isOpen.flatMapLatest { isOpen ->
            flow<Result<List<MarketMover>, DataError.Network>> {
                while (true) {
                    val losers = api.getLosers().asExternalModel()
                    emit(Result.Success(losers))

                    val refreshInterval =
                        if (isOpen) MARKET_DATA_REFRESH_OPEN else MARKET_DATA_REFRESH_CLOSED // 20 seconds or 10 minutes
                    delay(refreshInterval)
                }
            }
        }.flowOn(Dispatchers.IO).catch { e -> emit(handleNetworkException(e)) }

    override val gainers: Flow<Result<List<MarketMover>, DataError.Network>> =
        isOpen.flatMapLatest { isOpen ->
            flow<Result<List<MarketMover>, DataError.Network>> {
                while (true) {
                    val gainers = api.getGainers().asExternalModel()
                    emit(Result.Success(gainers))

                    val refreshInterval =
                        if (isOpen) MARKET_DATA_REFRESH_OPEN else MARKET_DATA_REFRESH_CLOSED // 20 seconds or 10 minutes
                    delay(refreshInterval)
                }
            }
        }.flowOn(Dispatchers.IO).catch { e -> emit(handleNetworkException(e)) }

    override val headlines: Flow<Result<List<News>, DataError.Network>> =
        isOpen.flatMapLatest { isOpen ->
            flow<Result<List<News>, DataError.Network>> {
                while (true) {
                    val news = api.getNews().asExternalModel()
                    emit(Result.Success(news))

                    val refreshInterval =
                        if (isOpen) SLOW_REFRESH_INTERVAL_OPEN else SLOW_REFRESH_INTERVAL_CLOSED // 30 minutes or 1 hr
                    delay(refreshInterval)
                }
            }
        }.flowOn(Dispatchers.IO).catch { e -> emit(handleNetworkException(e)) }

    override val sectors: Flow<Result<List<MarketSector>, DataError.Network>> =
        isOpen.flatMapLatest { isOpen ->
            flow<Result<List<MarketSector>, DataError.Network>> {
                while (true) {
                    val sectors = api.getSectors().asExternalModel()
                    emit(Result.Success(sectors))

                    val refreshInterval =
                        if (isOpen) SLOW_REFRESH_INTERVAL_OPEN else NEVER_REFRESH_INTERVAL // 30 minutes or never
                    delay(refreshInterval)
                }
            }
        }.flowOn(Dispatchers.IO).catch { e -> emit(handleNetworkException(e)) }
}