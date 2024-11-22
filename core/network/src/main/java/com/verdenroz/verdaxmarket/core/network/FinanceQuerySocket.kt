package com.verdenroz.verdaxmarket.core.network

import kotlinx.coroutines.channels.Channel

interface FinanceQuerySocket<T, P> {
    companion object {
        internal const val SOCKET_URL = "wss://finance-query.onrender.com"
    }

    /**
     * Connect to the socket and return a channel for receiving messages
     * @param params Connection parameters (e.g., symbol for ProfileSocket)
     * @return Channel that will receive socket messages
     */
    suspend fun connect(params: P): Channel<T?>

    /**
     * Disconnect from the socket
     * @param params Parameters needed for disconnection (e.g., symbol for ProfileSocket)
     */
    suspend fun disconnect(params: P)
}