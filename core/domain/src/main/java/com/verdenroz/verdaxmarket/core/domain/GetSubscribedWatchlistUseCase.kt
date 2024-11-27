package com.verdenroz.verdaxmarket.core.domain

import com.verdenroz.verdaxmarket.core.common.dispatchers.Dispatcher
import com.verdenroz.verdaxmarket.core.common.dispatchers.FinanceQueryDispatchers
import com.verdenroz.verdaxmarket.core.common.error.DataError
import com.verdenroz.verdaxmarket.core.common.result.Result
import com.verdenroz.verdaxmarket.core.data.model.asExternalModel
import com.verdenroz.verdaxmarket.core.data.repository.SocketRepository
import com.verdenroz.verdaxmarket.core.data.repository.WatchlistRepository
import com.verdenroz.verdaxmarket.core.data.utils.MarketMonitor
import com.verdenroz.verdaxmarket.core.data.utils.handleNetworkException
import com.verdenroz.verdaxmarket.core.model.SimpleQuoteData
import com.verdenroz.verdaxmarket.core.network.FinanceQueryDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetSubscribedWatchlistUseCase @Inject constructor(
    private val socket: SocketRepository,
    private val api: FinanceQueryDataSource,
    private val watchlistRepository: WatchlistRepository,
    private val marketMonitor: MarketMonitor,
    @Dispatcher(FinanceQueryDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) {

    companion object {
        private const val MARKET_DATA_REFRESH_OPEN = 15000L // 15 seconds
        private const val MARKET_DATA_REFRESH_CLOSED = 300000L // 5 minutes
    }

    /**
     * Returns a flow of the watchlist data for the given list of symbols
     * If there is an error in the socket, it will manually poll the API
     */
    operator fun invoke(symbols: List<String>): Flow<Result<List<SimpleQuoteData>, DataError.Network>> =
        socket.getWatchlist(symbols).flatMapLatest { socketResult ->
            when (socketResult) {
                is Result.Success -> {
                    flowOf(Result.Success(socketResult.data))
                }
                is Result.Loading -> flowOf(Result.Loading())
                else -> marketMonitor.isMarketOpen.flatMapLatest { isOpen ->
                    flow {
                        while (true) {
                            val updatedQuotes = api.getBulkQuote(symbols).asExternalModel()
                            emit(Result.Success(updatedQuotes))

                            when (isOpen) {
                                true -> delay(MARKET_DATA_REFRESH_OPEN)
                                false -> delay(MARKET_DATA_REFRESH_CLOSED)
                            }
                        }
                    }
                }
            }
        }
            .flowOn(ioDispatcher)
            .catch { e -> emit(Result.Error(handleNetworkException(e))) }
}