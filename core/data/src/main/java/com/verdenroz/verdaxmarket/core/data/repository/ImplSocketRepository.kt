package com.verdenroz.verdaxmarket.core.data.repository

import com.verdenroz.verdaxmarket.core.common.error.DataError
import com.verdenroz.verdaxmarket.core.common.result.Result
import com.verdenroz.verdaxmarket.core.data.model.asExternalModel
import com.verdenroz.verdaxmarket.core.data.utils.ExceptionHandler
import com.verdenroz.verdaxmarket.core.data.utils.catchAndEmitError
import com.verdenroz.verdaxmarket.core.model.MarketInfo
import com.verdenroz.verdaxmarket.core.model.Profile
import com.verdenroz.verdaxmarket.core.model.SimpleQuoteData
import com.verdenroz.verdaxmarket.core.network.sockets.MarketSocket
import com.verdenroz.verdaxmarket.core.network.sockets.ProfileSocket
import com.verdenroz.verdaxmarket.core.network.sockets.QuoteSocket
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImplSocketRepository @Inject constructor(
    private val profileSocket: ProfileSocket,
    private val marketSocket: MarketSocket,
    private val quoteSocket: QuoteSocket,
    private val exceptionHandler: ExceptionHandler
) : SocketRepository {

    override val market: Flow<Result<MarketInfo, DataError.Network>> = websocketFlow(
        connect = { marketSocket.connect(Unit) },
        disconnect = { marketSocket.disconnect(Unit) },
        transform = { response ->
            if (response != null) {
                Result.Success(response.asExternalModel())
            } else {
                Result.Error(DataError.Network.SERVER_DOWN)
            }
        },
    )

    override fun getProfile(symbol: String): Flow<Result<Profile, DataError.Network>> = websocketFlow(
        connect = { profileSocket.connect(symbol) },
        disconnect = { profileSocket.disconnect(symbol) },
        transform = { response ->
            if (response != null) {
                Result.Success(response.asExternalModel())
            } else {
                Result.Error(DataError.Network.SERVER_DOWN)
            }
        },
    )

    override fun getQuotes(symbols: List<String>): Flow<Result<List<SimpleQuoteData>, DataError.Network>> = websocketFlow(
        connect = { quoteSocket.connect(mapOf("symbols" to symbols.joinToString(","))) },
        disconnect = { quoteSocket.disconnect(mapOf("symbols" to symbols.joinToString(","))) },
        transform = { response ->
            val quotes = response?.asExternalModel()
            if (quotes != null) {
                Result.Success(quotes)
            } else {
                Result.Error(DataError.Network.SERVER_DOWN)
            }
        },
    )

    private fun <T, R> websocketFlow(
        connect: suspend () -> ReceiveChannel<T>,
        disconnect: suspend () -> Unit,
        transform: (T?) -> Result<R, DataError.Network>,
    ): Flow<Result<R, DataError.Network>> = channelFlow {
        val channel = connect()
        try {
            for (response in channel) {
                trySend(transform(response))
            }
        } finally {
            disconnect()
        }
    }.catchAndEmitError(exceptionHandler)
}