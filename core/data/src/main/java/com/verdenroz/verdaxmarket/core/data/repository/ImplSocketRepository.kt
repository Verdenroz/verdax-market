package com.verdenroz.verdaxmarket.core.data.repository

import com.verdenroz.verdaxmarket.core.common.error.DataError
import com.verdenroz.verdaxmarket.core.common.result.Result
import com.verdenroz.verdaxmarket.core.data.model.asExternalModel
import com.verdenroz.verdaxmarket.core.data.utils.handleNetworkException
import com.verdenroz.verdaxmarket.core.model.MarketInfo
import com.verdenroz.verdaxmarket.core.model.Profile
import com.verdenroz.verdaxmarket.core.model.SimpleQuoteData
import com.verdenroz.verdaxmarket.core.network.FinanceQuerySocket
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
            trySend(Result.Success(marketInfoResponse.asExternalModel()))
        }
        awaitClose { marketSocket.close() }
    }.catch { e -> handleNetworkException(e) }

    override fun getProfile(symbol: String): Flow<Result<Profile, DataError.Network>> = callbackFlow {
        profileSocket.open(mapOf("symbol" to symbol))
        profileSocket.setOnNewMessageListener { profileResponse ->
            trySend(Result.Success(profileResponse.asExternalModel()))
        }
        awaitClose { profileSocket.close() }
    }.catch { e -> handleNetworkException(e) }


    override fun getWatchlist(symbols: List<String>): Flow<Result<List<SimpleQuoteData>, DataError.Network>> = callbackFlow {
        watchlistSocket.open(mapOf("symbols" to symbols.joinToString(",")))
        watchlistSocket.setOnNewMessageListener { simpleQuoteResponse ->
            val quotes = simpleQuoteResponse.map { it.asExternalModel() }
            trySend(Result.Success(quotes))
        }
        awaitClose { watchlistSocket.close() }
    }.catch { e -> handleNetworkException(e) }

}