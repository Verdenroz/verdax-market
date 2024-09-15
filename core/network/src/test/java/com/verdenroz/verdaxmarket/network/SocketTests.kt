package com.verdenroz.verdaxmarket.network

import com.verdenroz.verdaxmarket.core.network.demo.DemoMarketSocket
import com.verdenroz.verdaxmarket.core.network.demo.DemoProfileSocket
import com.verdenroz.verdaxmarket.core.network.demo.DemoWatchlistSocket
import com.verdenroz.verdaxmarket.core.network.sockets.MarketSocket
import com.verdenroz.verdaxmarket.core.network.sockets.ProfileSocket
import com.verdenroz.verdaxmarket.core.network.sockets.WatchlistSocket
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class SocketTests {
    private lateinit var marketSocket: MarketSocket
    private lateinit var profileSocket: ProfileSocket
    private lateinit var watchlistSocket: WatchlistSocket

    @Before
    fun setup() {
        marketSocket = DemoMarketSocket().invoke()
        profileSocket = DemoProfileSocket().invoke()
        watchlistSocket = DemoWatchlistSocket().invoke()
    }

    @Test
    fun testMarketSocket() {
        runBlocking {
            marketSocket.open(emptyMap())
            marketSocket.setOnNewMessageListener { marketInfoResponse ->
                println(marketInfoResponse)
            }
            delay(20000)
            marketSocket.close()

            assert(marketSocket.webSocket == null)
        }
    }

    @Test
    fun testProfileSocket() {
        runBlocking {
            profileSocket.open(mapOf("symbol" to "NVDA"))
            profileSocket.setOnNewMessageListener { profileResponse ->
                println(profileResponse)
            }
            delay(20000)
            profileSocket.close()

            assert(profileSocket.webSocket == null)
        }
    }

    @Test
    fun testWatchlistSocket() {
        runBlocking {
            watchlistSocket.open(mapOf("symbols" to "AAPL,MSFT,GOOGL"))
            watchlistSocket.setOnNewMessageListener { simpleQuoteResponse ->
                println(simpleQuoteResponse)
            }
            delay(20000)
            watchlistSocket.close()

            assert(watchlistSocket.webSocket == null)
        }
    }
}