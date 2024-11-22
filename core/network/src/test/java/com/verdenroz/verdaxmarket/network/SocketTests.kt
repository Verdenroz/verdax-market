package com.verdenroz.verdaxmarket.network

import com.verdenroz.verdaxmarket.core.network.demo.DemoMarketSocket
import com.verdenroz.verdaxmarket.core.network.demo.DemoProfileSocket
import com.verdenroz.verdaxmarket.core.network.demo.DemoWatchlistSocket
import com.verdenroz.verdaxmarket.core.network.model.ProfileResponse
import com.verdenroz.verdaxmarket.core.network.sockets.MarketSocket
import com.verdenroz.verdaxmarket.core.network.sockets.ProfileSocket
import com.verdenroz.verdaxmarket.core.network.sockets.WatchlistSocket
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

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

    @After
    fun teardown() = runBlocking {
        marketSocket.disconnect(Unit)
        profileSocket.disconnect("AAPL")
        watchlistSocket.disconnect(mapOf("symbols" to "AAPL,MSFT,GOOGL"))
    }

    @Test
    fun testMarketSocket() = runBlocking {
        val receivedData = AtomicBoolean(false)

        withTimeout(21000) { // 21 seconds to account for setup time
            val channel = marketSocket.connect(Unit)

            val job = launch {
                var messageCount = 0
                for (message in channel) {
                    assertNotNull(message) { "Received null market data" }
                    messageCount++
                    receivedData.set(true)
                    println("Received market data #$messageCount: $message")
                }
            }

            // Wait for 20 seconds
            kotlinx.coroutines.delay(20000)
            marketSocket.disconnect(Unit)
            job.cancel()
        }

        assertTrue(receivedData.get(), "Should have received market data")
    }

    @Test
    fun testProfileSocket() = runBlocking {
        val receivedData = AtomicBoolean(false)
        val testSymbol = "NVDA"

        withTimeout(21000) {
            val channel = profileSocket.connect(testSymbol)

            val job = launch {
                var messageCount = 0
                for (message in channel) {
                    assertNotNull(message) { "Received null profile data" }
                    assertEquals(testSymbol, message.quote.symbol, "Received data for wrong symbol")
                    messageCount++
                    receivedData.set(true)
                    println("Received profile data #$messageCount for $testSymbol: $message")
                }
            }

            // Wait for 20 seconds
            kotlinx.coroutines.delay(20000)
            profileSocket.disconnect(testSymbol)
            job.cancel()
        }

        assertTrue(receivedData.get(), "Should have received profile data")
    }

    @Test
    fun testWatchlistSocket() = runBlocking {
        val receivedData = AtomicBoolean(false)
        val symbols = listOf("NVDA", "MSFT", "GOOGL")
        val params = mapOf("symbols" to symbols.joinToString(","))

        withTimeout(21000) {
            val channel = watchlistSocket.connect(params)

            val job = launch {
                var messageCount = 0
                for (message in channel) {
                    assertNotNull(message) { "Received null watchlist data" }
                    assertTrue(message.isNotEmpty(), "Received empty watchlist data")
                    assertTrue(
                        message.all { it.symbol in symbols },
                        "Received data for unexpected symbol"
                    )
                    messageCount++
                    receivedData.set(true)
                    println("Received watchlist data #$messageCount: $message")
                }
            }

            // Wait for 20 seconds
            kotlinx.coroutines.delay(20000)
            watchlistSocket.disconnect(params)
            job.cancel()
        }

        assertTrue(receivedData.get(), "Should have received watchlist data")
    }

    @Test
    fun testMultipleSockets() = runBlocking {
        val symbols = listOf("AAPL", "MSFT", "GOOGL")
        val channels = mutableListOf<Channel<ProfileResponse?>>()
        val receivedData = mutableMapOf<String, AtomicBoolean>()

        symbols.forEach { symbol ->
            receivedData[symbol] = AtomicBoolean(false)
        }

        withTimeout(21000) {
            // Start connections for all symbols
            symbols.forEach { symbol ->
                val channel = profileSocket.connect(symbol)
                channels.add(channel)

                launch {
                    var messageCount = 0
                    for (message in channel) {
                        assertNotNull(message) { "Received null profile data for $symbol" }
                        assertEquals(symbol, message.quote.symbol, "Received data for wrong symbol")
                        messageCount++
                        receivedData[symbol]?.set(true)
                        println("Received profile data #$messageCount for $symbol: $message")
                    }
                }
            }

            // Wait for 20 seconds
            kotlinx.coroutines.delay(20000)

            // Disconnect all
            symbols.forEach { symbol ->
                profileSocket.disconnect(symbol)
            }
        }

        // Verify we received data for all symbols
        receivedData.forEach { (symbol, received) ->
            assertTrue(received.get(), "Should have received data for $symbol")
        }
    }
}