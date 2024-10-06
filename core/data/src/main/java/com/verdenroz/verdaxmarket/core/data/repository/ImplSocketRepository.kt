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
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImplSocketRepository @Inject constructor(
    private val profileSocket: ProfileSocket,
    private val marketSocket: MarketSocket,
    private val watchlistSocket: WatchlistSocket
) : SocketRepository {

    override val market: Flow<Result<MarketInfo, DataError.Network>> = callbackFlow {
        marketSocket.open(emptyMap())
        marketSocket.setOnNewMessageListener { marketInfoResponse ->
            trySend(
                if (marketInfoResponse != null) {
                    Result.Success(marketInfoResponse.asExternalModel())
                } else {
                    Result.Error(DataError.Network.SERVER_DOWN)
                }
            ).isSuccess
        }
        awaitClose { marketSocket.close() }
    }.catch { e -> handleNetworkException(e) }

    override fun getProfile(symbol: String): Flow<Result<Profile, DataError.Network>> = callbackFlow {
        profileSocket.open(mapOf("symbol" to symbol))
        profileSocket.setOnNewMessageListener { profileResponse ->
            trySend(
                if (profileResponse != null) {
                    Result.Success(profileResponse.asExternalModel())
                } else {
                    Result.Error(DataError.Network.SERVER_DOWN)
                }
            ).isSuccess
        }
        awaitClose { profileSocket.close() }
    }.catch { e -> handleNetworkException(e) }


    override fun getWatchlist(symbols: List<String>): Flow<Result<List<SimpleQuoteData>, DataError.Network>> = callbackFlow {
        watchlistSocket.open(mapOf("symbols" to symbols.joinToString(",")))
        watchlistSocket.setOnNewMessageListener { simpleQuoteResponse ->
            val quotes = simpleQuoteResponse?.asExternalModel()
            trySend(
                if (quotes != null) {
                    Result.Success(quotes)
                } else {
                    Result.Error(DataError.Network.SERVER_DOWN)
                }
            ).isSuccess
        }
        awaitClose { watchlistSocket.close() }
    }.catch { e -> handleNetworkException(e) }

}