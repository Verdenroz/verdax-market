package com.verdenroz.verdaxmarket.core.network.sockets

import com.verdenroz.verdaxmarket.core.network.BuildConfig
import com.verdenroz.verdaxmarket.core.network.FinanceQuerySocket
import com.verdenroz.verdaxmarket.core.network.FinanceQuerySocket.Companion.SOCKET_URL
import com.verdenroz.verdaxmarket.core.network.model.ProfileResponse
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
) : FinanceQuerySocket<ProfileResponse?>, WebSocketListener() {

    override var webSocket: WebSocket? = null
    override var messageListener: ((ProfileResponse?) -> Unit)? = null

    override fun setOnNewMessageListener(listener: (ProfileResponse?) -> Unit) {
        messageListener = listener
    }

    override suspend fun open(params: Map<String, String>) {
        val symbol = params["symbol"] ?: throw IllegalArgumentException("Symbol parameter is required")
        val url = "$SOCKET_URL/profile/$symbol"
        val request = Request.Builder()
            .url(url)
            .addHeader("x-api-key", BuildConfig.financeQueryAPIKey)
            .build()
        webSocket = client.newWebSocket(request, this)
    }

    override fun close() {
        webSocket?.close(1000, null)
        webSocket = null
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        val profileResponse = parser.decodeFromString<ProfileResponse>(text)
        messageListener?.invoke(profileResponse)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        messageListener?.invoke(null)
        webSocket.close(1000, t.message)
    }
}