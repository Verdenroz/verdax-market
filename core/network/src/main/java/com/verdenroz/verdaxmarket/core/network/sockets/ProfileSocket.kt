package com.verdenroz.verdaxmarket.core.network.sockets

import com.verdenroz.verdaxmarket.core.network.BuildConfig
import com.verdenroz.verdaxmarket.core.network.FinanceQuerySocket
import com.verdenroz.verdaxmarket.core.network.FinanceQuerySocket.Companion.SOCKET_URL
import com.verdenroz.verdaxmarket.core.network.model.ProfileResponse
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
class ProfileSocket @Inject constructor(
    private val parser: Json,
    private val client: OkHttpClient
) : FinanceQuerySocket<ProfileResponse?, String>, WebSocketListener() {

    private val connections = mutableMapOf<String, WebSocket>()
    private val channels = mutableMapOf<String, Channel<ProfileResponse?>>()
    private val mutex = Mutex()

    override suspend fun connect(params: String): Channel<ProfileResponse?> = mutex.withLock {
        if (connections.containsKey(params)) {
            return@withLock channels[params]!!
        }

        val newChannel = Channel<ProfileResponse?>(Channel.BUFFERED)
        val url = "$SOCKET_URL/profile/$params"
        val request = Request.Builder()
            .url(url)
            .addHeader("x-api-key", BuildConfig.FINANCE_QUERY_API_KEY)
            .build()

        val webSocket = client.newWebSocket(request, this)
        connections[params] = webSocket
        channels[params] = newChannel

        return@withLock newChannel
    }

    override suspend fun disconnect(params: String) {
        mutex.withLock {
            connections[params]?.close(1000, null)
            connections.remove(params)
            channels[params]?.close()
            channels.remove(params)
        }
    }


    override fun onMessage(webSocket: WebSocket, text: String) {
        val profileResponse = parser.decodeFromString<ProfileResponse>(text)
        connections.entries.find { it.value == webSocket }?.key?.let { key ->
            channels[key]?.trySend(profileResponse)
        }
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        connections.entries.find { it.value == webSocket }?.key?.let { key ->
            channels[key]?.trySend(null)
            connections.remove(key)
            channels.remove(key)
        }
        webSocket.close(1000, t.message)
    }
}