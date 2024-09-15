package com.verdenroz.verdaxmarket.core.network

import okhttp3.WebSocket

interface FinanceQuerySocket<T> {

    var webSocket: WebSocket?
    var messageListener: ((T) -> Unit)?

    companion object {
        internal const val SOCKET_URL = BuildConfig.socketURL
    }

    /**
     * Set a listener for new messages
     * @param listener Function to be called when a new message is received
     */
    fun setOnNewMessageListener(listener: (T) -> Unit)

    /**
     * Open a socket connection
     * @param params Map of parameters to be sent to the socket
     */
    suspend fun open(params: Map<String, String>)

    /**
     * Close the socket connection
     */
    fun close()
}