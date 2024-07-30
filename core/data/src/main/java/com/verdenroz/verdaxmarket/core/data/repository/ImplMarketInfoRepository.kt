package com.verdenroz.verdaxmarket.core.data.repository

import com.verdenroz.verdaxmarket.core.common.dispatchers.Dispatcher
import com.verdenroz.verdaxmarket.core.common.dispatchers.FinanceQueryDispatchers
import com.verdenroz.verdaxmarket.core.common.error.DataError
import com.verdenroz.verdaxmarket.core.common.result.Result
import com.verdenroz.verdaxmarket.core.data.model.asExternalModel
import com.verdenroz.verdaxmarket.core.data.utils.MarketStatusMonitor
import com.verdenroz.verdaxmarket.core.data.utils.handleNetworkException
import com.verdenroz.verdaxmarket.core.model.MarketIndex
import com.verdenroz.verdaxmarket.core.model.MarketMover
import com.verdenroz.verdaxmarket.core.model.MarketSector
import com.verdenroz.verdaxmarket.core.model.News
import com.verdenroz.verdaxmarket.core.network.FinanceQueryDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

class ImplMarketInfoRepository @Inject constructor(
    private val api: FinanceQueryDataSource,
    marketStatusMonitor: MarketStatusMonitor,
    @Dispatcher(FinanceQueryDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) : MarketInfoRepository {

    override val marketStatus: Flow<Boolean> = marketStatusMonitor.isMarketOpen.stateIn(
        CoroutineScope(ioDispatcher),
        SharingStarted.WhileSubscribed(5000L),
        true
    )

    override val indices: Flow<Result<List<MarketIndex>, DataError.Network>> = combine(
        marketStatus,
        flow {
            while (true) {
                try {
                    val quotes = api.getIndexes().asExternalModel()
                    emit(Result.Success(quotes))
                } catch (e: Exception) {
                    emit(handleNetworkException(e))
                }
            }
        }
    ) { isOpen, result ->
        Pair(isOpen, result)
    }.flatMapLatest { (isOpen, result) ->
        flow {
            emit(result)
            val refreshInterval = if (isOpen) 15000L else 600000L // 15 seconds or 10 minute
            delay(refreshInterval)
        }
    }.flowOn(ioDispatcher)

    override val actives: Flow<Result<List<MarketMover>, DataError.Network>> = combine(
        marketStatus,
        flow {
            while (true) {
                try {
                    val quotes = api.getActives().asExternalModel()
                    emit(Result.Success(quotes))
                } catch (e: Exception) {
                    emit(handleNetworkException(e))
                }
            }
        }
    ) { isOpen, result ->
        Pair(isOpen, result)
    }.flatMapLatest { (isOpen, result) ->
        flow {
            emit(result)
            val refreshInterval = if (isOpen) 15000L else 600000L // 15 seconds or 10 minute
            delay(refreshInterval)
        }
    }.flowOn(ioDispatcher)

    override val losers: Flow<Result<List<MarketMover>, DataError.Network>> = combine(
        marketStatus,
        flow {
            while (true) {
                try {
                    val quotes = api.getLosers().asExternalModel()
                    emit(Result.Success(quotes))
                } catch (e: Exception) {
                    emit(handleNetworkException(e))
                }
            }
        }
    ) { isOpen, result ->
        Pair(isOpen, result)
    }.flatMapLatest { (isOpen, result) ->
        flow {
            emit(result)
            val refreshInterval = if (isOpen) 15000L else 600000L // 15 seconds or 10 minute
            delay(refreshInterval)
        }
    }.flowOn(ioDispatcher)

    override val gainers: Flow<Result<List<MarketMover>, DataError.Network>> = combine(
        marketStatus,
        flow {
            while (true) {
                try {
                    val quotes = api.getGainers().asExternalModel()
                    emit(Result.Success(quotes))
                } catch (e: Exception) {
                    emit(handleNetworkException(e))
                }
            }
        }
    ) { isOpen, result ->
        Pair(isOpen, result)
    }.flatMapLatest { (isOpen, result) ->
        flow {
            emit(result)
            val refreshInterval = if (isOpen) 15000L else 600000L // 15 seconds or 10 minute
            delay(refreshInterval)
        }
    }.flowOn(ioDispatcher)

    override val headlines: Flow<Result<List<News>, DataError.Network>> = combine(
        marketStatus,
        flow {
            while (true) {
                try {
                    val quotes = api.getNews().asExternalModel()
                    emit(Result.Success(quotes))
                } catch (e: Exception) {
                    emit(handleNetworkException(e))
                }
            }
        }
    ) { isOpen, result ->
        Pair(isOpen, result)
    }.flatMapLatest { (isOpen, result) ->
        flow {
            emit(result)
            val refreshInterval = if (isOpen) 15000L else 600000L // 15 seconds or 10 minute
            delay(refreshInterval)
        }
    }.flowOn(ioDispatcher)

    override val sectors: Flow<Result<List<MarketSector>, DataError.Network>> = combine(
        marketStatus,
        flow {
            while (true) {
                try {
                    val quotes = api.getSectors().asExternalModel()
                    emit(Result.Success(quotes))
                } catch (e: Exception) {
                    emit(handleNetworkException(e))
                }
            }
        }
    ) { isOpen, result ->
        Pair(isOpen, result)
    }.flatMapLatest { (isOpen, result) ->
        flow {
            emit(result)
            val refreshInterval: Long = if (isOpen) 15000L else 600000L // 15 seconds or 10 minute
            delay(refreshInterval)
        }
    }.flowOn(ioDispatcher)
}