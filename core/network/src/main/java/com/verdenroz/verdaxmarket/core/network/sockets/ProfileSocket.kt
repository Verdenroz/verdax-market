package com.verdenroz.verdaxmarket.core.network.sockets

import android.util.Log
import com.verdenroz.verdaxmarket.core.network.BuildConfig
import com.verdenroz.verdaxmarket.core.network.FinanceQuerySocket
import com.verdenroz.verdaxmarket.core.network.FinanceQuerySocket.Companion.SOCKET_URL
import com.verdenroz.verdaxmarket.core.network.model.ProfileResponse
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
class ProfileSocket @Inject constructor(
    private val parser: Json,
    private val client: OkHttpClient
) : FinanceQuerySocket<ProfileResponse?, String>, WebSocketListener() {

    private val activeConnections = mutableMapOf<String, ConnectionInfo>()
    private val mutex = Mutex()

    private data class ConnectionInfo(
        val webSocket: WebSocket,
        val channel: Channel<ProfileResponse?>
    )

    override suspend fun connect(params: String): Channel<ProfileResponse?> = mutex.withLock {
        // Close existing connection if it exists
        activeConnections[params]?.let { connectionInfo ->
            Log.d("ProfileSocket", "Closing existing connection for $params")
            connectionInfo.webSocket.close(1000, "New connection requested")
            connectionInfo.channel.close()
            activeConnections.remove(params)
        }

        val channel = Channel<ProfileResponse?>(Channel.BUFFERED)
        val url = "$SOCKET_URL/profile/$params"
        val request = Request.Builder()
            .url(url)
            .addHeader("x-api-key", BuildConfig.financeQueryAPIKey)
            .build()

        val webSocket = client.newWebSocket(request, this)
        activeConnections[params] = ConnectionInfo(webSocket, channel)

        Log.d("ProfileSocket", "New connection created for $params")
        return@withLock channel
    }

    override suspend fun disconnect(params: String): Unit = mutex.withLock {
        activeConnections[params]?.let { connectionInfo ->
            connectionInfo.webSocket.close(1000, null)
            connectionInfo.channel.close()
            activeConnections.remove(params)
            Log.d("ProfileSocket", "Connection closed for $params")
        }
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        val profileResponse = parser.decodeFromString<ProfileResponse>(text)
        Log.d("ProfileSocket", "Received profile response for $profileResponse")
        activeConnections.values.find { it.webSocket == webSocket }?.channel?.trySend(
            profileResponse
        )
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        activeConnections.entries.find { it.value.webSocket == webSocket }
            ?.let { (_, connectionInfo) ->
                connectionInfo.channel.trySend(null)
                webSocket.close(1000, t.message)
                // Don't remove from activeConnections here - let disconnect handle that
            }
    }
}