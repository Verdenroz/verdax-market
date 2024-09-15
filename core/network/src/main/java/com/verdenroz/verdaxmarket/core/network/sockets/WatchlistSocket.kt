package com.verdenroz.verdaxmarket.core.network.sockets

import com.verdenroz.verdaxmarket.core.network.FinanceQuerySocket
import com.verdenroz.verdaxmarket.core.network.FinanceQuerySocket.Companion.SOCKET_URL
import com.verdenroz.verdaxmarket.core.network.model.SimpleQuoteResponse
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
) : FinanceQuerySocket<List<SimpleQuoteResponse>>, WebSocketListener() {

    override var webSocket: WebSocket? = null
    override var messageListener: ((List<SimpleQuoteResponse>) -> Unit)? = null

    override fun setOnNewMessageListener(listener: (List<SimpleQuoteResponse>) -> Unit) {
        messageListener = listener
    }

    override suspend fun open(params: Map<String, String>) {
        val symbols = params["symbols"]?.split(",") ?: throw IllegalArgumentException("Symbols parameter is required")
        val url = "$SOCKET_URL/quotes"
        val request = Request.Builder()
            .url(url)
            .build()
        webSocket = client.newWebSocket(request, this)
        val symString = symbols.joinToString(",")
        webSocket!!.send(symString)
    }

    override fun close() {
        webSocket?.close(1000, null)
        webSocket = null
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        val quoteResponse = parser.decodeFromString<List<SimpleQuoteResponse>>(text)
        messageListener?.invoke(quoteResponse)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        t.printStackTrace()
        webSocket.close(1000, t.message)
    }
}