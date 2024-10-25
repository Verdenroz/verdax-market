package com.verdenroz.verdaxmarket.core.network.sockets

import com.verdenroz.verdaxmarket.core.network.BuildConfig
import com.verdenroz.verdaxmarket.core.network.FinanceQuerySocket
import com.verdenroz.verdaxmarket.core.network.FinanceQuerySocket.Companion.SOCKET_URL
import com.verdenroz.verdaxmarket.core.network.model.MarketInfoResponse
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MarketSocket @Inject constructor(
    private val parser: Json,
    private val client: OkHttpClient
) : FinanceQuerySocket<MarketInfoResponse, Unit>, WebSocketListener() {

    private var webSocket: WebSocket? = null
    private var channel: Channel<MarketInfoResponse?>? = null
    private val mutex = Mutex()

    override suspend fun connect(params: Unit): Channel<MarketInfoResponse?> = mutex.withLock {
        channel = Channel(Channel.BUFFERED)
        val url = "$SOCKET_URL/market"
        val request = Request.Builder()
            .url(url)
            .addHeader("x-api-key", BuildConfig.financeQueryAPIKey)
            .build()

        webSocket = client.newWebSocket(request, this)

        return@withLock channel!!
    }

    override suspend fun disconnect(params: Unit) = mutex.withLock {
        webSocket?.close(1000, null)
        webSocket = null
        channel?.close()
        channel = null
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        val marketResponse = parser.decodeFromString<MarketInfoResponse>(text)
        channel?.trySend(marketResponse)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        channel?.trySend(null)
        webSocket.close(1000, t.message)
    }
}