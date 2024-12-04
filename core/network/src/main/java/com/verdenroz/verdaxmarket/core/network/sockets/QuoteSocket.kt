package com.verdenroz.verdaxmarket.core.network.sockets

import com.verdenroz.verdaxmarket.core.network.BuildConfig
import com.verdenroz.verdaxmarket.core.network.FinanceQuerySocket
import com.verdenroz.verdaxmarket.core.network.FinanceQuerySocket.Companion.SOCKET_URL
import com.verdenroz.verdaxmarket.core.network.model.SimpleQuoteResponse
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
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

    private val connections = mutableMapOf<String, WebSocket>()
    private val channels = mutableMapOf<String, Channel<List<SimpleQuoteResponse>?>>()
    private val mutex = Mutex()

    override suspend fun connect(params: Map<String, String>): Channel<List<SimpleQuoteResponse>?> = mutex.withLock {
        val symbols = params["symbols"] ?: throw IllegalArgumentException("Symbols parameter is required")
        if (connections.containsKey(symbols)) {
            return@withLock channels[symbols]!!
        }

        val newChannel = Channel<List<SimpleQuoteResponse>?>(Channel.BUFFERED)
        val url = "$SOCKET_URL/quotes"
        val request = Request.Builder()
            .url(url)
            .addHeader("x-api-key", BuildConfig.FINANCE_QUERY_API_KEY)
            .build()

        val webSocket = client.newWebSocket(request, this)
        connections[symbols] = webSocket
        channels[symbols] = newChannel

        webSocket.send(symbols)

        return@withLock newChannel
    }

    override suspend fun disconnect(params: Map<String, String>) = mutex.withLock {
        val symbols = params["symbols"] ?: return@withLock
        connections[symbols]?.close(1000, null)
        connections.remove(symbols)
        channels[symbols]?.close()
        channels.remove(symbols)
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        val quoteResponse = parser.decodeFromString<List<SimpleQuoteResponse>>(text)
        val responseSymbolSet = quoteResponse.map { it.symbol }.toSet()

        channels.forEach { (channelSymbols, channel) ->
            val channelSymbolsSet = channelSymbols.split(",").toSet()
            if (channelSymbolsSet == responseSymbolSet) {
                Log.d("QuoteSocket", "Received exact match quotes for $responseSymbolSet on channel $channelSymbolsSet")
                channel.trySend(quoteResponse)
            }
        }
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        channels.values.forEach { it.trySend(null) }
        webSocket.close(1000, t.message)
    }
}