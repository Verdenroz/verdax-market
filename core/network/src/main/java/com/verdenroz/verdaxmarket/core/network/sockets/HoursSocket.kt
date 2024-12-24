package com.verdenroz.verdaxmarket.core.network.sockets

import com.verdenroz.verdaxmarket.core.network.BuildConfig
import com.verdenroz.verdaxmarket.core.network.FinanceQuerySocket
import com.verdenroz.verdaxmarket.core.network.FinanceQuerySocket.Companion.SOCKET_URL
import com.verdenroz.verdaxmarket.core.network.model.HoursInfoResponse
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import javax.inject.Singleton

@Singleton
class HoursSocket(
    private val parser: Json,
    private val client: OkHttpClient
) : FinanceQuerySocket<HoursInfoResponse, Unit>, WebSocketListener() {

    private var webSocket: WebSocket? = null
    private var channel: Channel<HoursInfoResponse?>? = null
    private val mutex = Mutex()

    override suspend fun connect(params: Unit): Channel<HoursInfoResponse?> = mutex.withLock {
        channel = Channel(Channel.BUFFERED)
        val url = "$SOCKET_URL/hours"
        val request = Request.Builder()
            .url(url)
            .addHeader("x-api-key", BuildConfig.FINANCE_QUERY_API_KEY)
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
        val hoursResponse = parser.decodeFromString<HoursInfoResponse>(text)
        channel?.trySend(hoursResponse)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        channel?.trySend(null)
        webSocket.close(1000, t.message)
    }
}