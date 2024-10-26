package com.verdenroz.verdaxmarket.core.network.sockets

import com.verdenroz.verdaxmarket.core.network.BuildConfig
import com.verdenroz.verdaxmarket.core.network.FinanceQuerySocket
import com.verdenroz.verdaxmarket.core.network.FinanceQuerySocket.Companion.SOCKET_URL
import com.verdenroz.verdaxmarket.core.network.model.SimpleQuoteResponse
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
class WatchlistSocket @Inject constructor(
    private val parser: Json,
    private val client: OkHttpClient
) : FinanceQuerySocket<List<SimpleQuoteResponse>, Map<String, String>>, WebSocketListener() {

    private var webSocket: WebSocket? = null
    private var channel: Channel<List<SimpleQuoteResponse>?>? = null
    private val mutex = Mutex()

    override suspend fun connect(params: Map<String, String>): Channel<List<SimpleQuoteResponse>?> = mutex.withLock {
        val symbols = params["symbols"]?.split(",") ?: throw IllegalArgumentException("Symbols parameter is required")
        val newChannel = Channel<List<SimpleQuoteResponse>?>(Channel.BUFFERED)
        val url = "$SOCKET_URL/quotes"
        val request = Request.Builder()
            .url(url)
            .addHeader("x-api-key", BuildConfig.financeQueryAPIKey)
            .build()

        webSocket = client.newWebSocket(request, this)
        channel = newChannel

        val symString = symbols.joinToString(",")
        webSocket!!.send(symString)

        return@withLock newChannel
    }

    override suspend fun disconnect(params: Map<String, String>) = mutex.withLock {
        webSocket?.close(1000, null)
        webSocket = null
        channel?.close()
        channel = null
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        val quoteResponse = parser.decodeFromString<List<SimpleQuoteResponse>>(text)
        channel?.trySend(quoteResponse)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        channel?.trySend(null)
        webSocket.close(1000, t.message)
    }
}