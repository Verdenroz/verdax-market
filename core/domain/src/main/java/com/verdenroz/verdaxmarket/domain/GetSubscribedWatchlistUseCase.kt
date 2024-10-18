package com.verdenroz.verdaxmarket.domain

import com.verdenroz.verdaxmarket.core.common.dispatchers.Dispatcher
import com.verdenroz.verdaxmarket.core.common.dispatchers.FinanceQueryDispatchers
import com.verdenroz.verdaxmarket.core.common.error.DataError
import com.verdenroz.verdaxmarket.core.common.result.Result
import com.verdenroz.verdaxmarket.core.data.repository.SocketRepository
import com.verdenroz.verdaxmarket.core.data.repository.WatchlistRepository
import com.verdenroz.verdaxmarket.core.data.utils.MarketMonitor
import com.verdenroz.verdaxmarket.core.data.utils.handleNetworkException
import com.verdenroz.verdaxmarket.core.model.SimpleQuoteData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetSubscribedWatchlistUseCase @Inject constructor(
    private val socket: SocketRepository,
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
    operator fun invoke(): Flow<Result<List<SimpleQuoteData>, DataError.Network>> =
        watchlistRepository.watchlist.flatMapConcat { watchlist ->
            val symbols = watchlist.map { it.symbol }
            if (symbols.isEmpty()) {
                return@flatMapConcat flowOf(Result.Success(emptyList()))
            }
            socket.getWatchlist(symbols).flatMapMerge { socketResult ->
                when (socketResult) {
                    is Result.Success -> {
                        watchlistRepository.updateWatchList(socketResult.data)
                        flowOf(Result.Success(socketResult.data))
                    }

                    is Result.Loading -> flowOf(Result.Loading())
                    else -> marketMonitor.isMarketOpen.flatMapLatest { isOpen ->
                        flow<Result<List<SimpleQuoteData>, DataError.Network>> {
                            while (true) {
                                watchlistRepository.updateWatchlist(symbols)
                                val currentWatchlist = watchlistRepository.getWatchlist().first()
                                emit(Result.Success(currentWatchlist))

                                when (isOpen) {
                                    true -> delay(MARKET_DATA_REFRESH_OPEN)
                                    false -> delay(MARKET_DATA_REFRESH_CLOSED)
                                }
                            }
                        }
                    }
                }
            }
        }.flowOn(ioDispatcher).catch { e -> handleNetworkException(e) }

}