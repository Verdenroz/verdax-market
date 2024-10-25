package com.verdenroz.verdaxmarket.core.data.repository

import com.verdenroz.verdaxmarket.core.common.error.DataError
import com.verdenroz.verdaxmarket.core.common.result.Result
import com.verdenroz.verdaxmarket.core.data.model.asExternalModel
import com.verdenroz.verdaxmarket.core.data.utils.handleNetworkException
import com.verdenroz.verdaxmarket.core.model.MarketInfo
import com.verdenroz.verdaxmarket.core.model.Profile
import com.verdenroz.verdaxmarket.core.model.SimpleQuoteData
import com.verdenroz.verdaxmarket.core.network.sockets.MarketSocket
import com.verdenroz.verdaxmarket.core.network.sockets.ProfileSocket
import com.verdenroz.verdaxmarket.core.network.sockets.WatchlistSocket
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImplSocketRepository @Inject constructor(
    private val profileSocket: ProfileSocket,
    private val marketSocket: MarketSocket,
    private val watchlistSocket: WatchlistSocket
) : SocketRepository {

    override val market: Flow<Result<MarketInfo, DataError.Network>> = channelFlow {
        val channel = marketSocket.connect(Unit)
        try {
            for (marketInfoResponse in channel) {
                trySend(
                    if (marketInfoResponse != null) {
                        Result.Success(marketInfoResponse.asExternalModel())
                    } else {
                        Result.Error(DataError.Network.SERVER_DOWN)
                    }
                )
            }
        } finally {
            marketSocket.disconnect(Unit)
        }
    }.catch { e -> emit(Result.Error(handleNetworkException(e))) }

    override fun getProfile(symbol: String): Flow<Result<Profile, DataError.Network>> = channelFlow {
        val channel = profileSocket.connect(symbol)
        try {
            for (profileResponse in channel) {
                trySend(
                    if (profileResponse != null) {
                        Result.Success(profileResponse.asExternalModel())
                    } else {
                        Result.Error(DataError.Network.SERVER_DOWN)
                    }
                )
            }
        } finally {
            profileSocket.disconnect(symbol)
        }
    }.catch { e -> emit(Result.Error(handleNetworkException(e))) }

    override fun getWatchlist(symbols: List<String>): Flow<Result<List<SimpleQuoteData>, DataError.Network>> = channelFlow {
        val params = mapOf("symbols" to symbols.joinToString(","))
        val channel = watchlistSocket.connect(params)
        try {
            for (simpleQuoteResponse in channel) {
                val quotes = simpleQuoteResponse?.asExternalModel()
                trySend(
                    if (quotes != null) {
                        Result.Success(quotes)
                    } else {
                        Result.Error(DataError.Network.SERVER_DOWN)
                    }
                )
            }
        } finally {
            watchlistSocket.disconnect(params)
        }
    }.catch { e -> emit(Result.Error(handleNetworkException(e))) }
}