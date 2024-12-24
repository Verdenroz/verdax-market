package com.verdenroz.verdaxmarket.network

import com.verdenroz.verdaxmarket.core.network.demo.DemoHoursSocket
import com.verdenroz.verdaxmarket.core.network.demo.DemoMarketSocket
import com.verdenroz.verdaxmarket.core.network.demo.DemoProfileSocket
import com.verdenroz.verdaxmarket.core.network.demo.DemoWatchlistSocket
import com.verdenroz.verdaxmarket.core.network.model.SimpleQuoteResponse
import com.verdenroz.verdaxmarket.core.network.sockets.HoursSocket
import com.verdenroz.verdaxmarket.core.network.sockets.MarketSocket
import com.verdenroz.verdaxmarket.core.network.sockets.ProfileSocket
import com.verdenroz.verdaxmarket.core.network.sockets.QuoteSocket
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
    private lateinit var quoteSocket: QuoteSocket
    private lateinit var hoursSocket: HoursSocket

    @Before
    fun setup() {
        marketSocket = DemoMarketSocket().invoke()
        profileSocket = DemoProfileSocket().invoke()
        quoteSocket = DemoWatchlistSocket().invoke()
        hoursSocket = DemoHoursSocket().invoke()
    }

    @After
    fun teardown() = runBlocking {
        marketSocket.disconnect(Unit)
        profileSocket.disconnect("AAPL")
        quoteSocket.disconnect(mapOf("symbols" to "AAPL,MSFT,GOOGL"))
    }

    @Test
    fun testMarketSocket() = runBlocking {
        val receivedData = AtomicBoolean(false)

        withTimeout(12000) { // 12 seconds to account for setup time
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
            kotlinx.coroutines.delay(11000)
            marketSocket.disconnect(Unit)
            job.cancel()
        }

        assertTrue(receivedData.get(), "Should have received market data")
    }

    @Test
    fun testProfileSocket() = runBlocking {
        val receivedData = AtomicBoolean(false)
        val testSymbol = "NVDA"

        withTimeout(12000) {
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

            // Wait for 11 seconds
            kotlinx.coroutines.delay(11000)
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

        withTimeout(12000) {
            val channel = quoteSocket.connect(params)

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

            // Wait for 11 seconds
            kotlinx.coroutines.delay(11000)
            quoteSocket.disconnect(params)
            job.cancel()
        }

        assertTrue(receivedData.get(), "Should have received watchlist data")
    }

    @Test
    fun testMultipleQuoteSockets() = runBlocking {
        val symbols = listOf("AAPL", "MSFT", "GOOGL")
        val channels = mutableListOf<Channel<List<SimpleQuoteResponse>?>>()
        val receivedData = mutableMapOf<String, AtomicBoolean>()

        symbols.forEach { symbol ->
            receivedData[symbol] = AtomicBoolean(false)
        }

        withTimeout(30000) {
            // Start connections for all symbols with a delay
            symbols.forEach { symbol ->
                val params = mapOf("symbols" to symbol)
                val channel = quoteSocket.connect(params)
                println("Connected to quote socket for $symbol")
                channels.add(channel)

                launch {
                    var messageCount = 0
                    for (message in channel) {
                        assertNotNull(message) { "Received null quote data for $symbol" }
                        assertTrue(message.any { it.symbol == symbol }, "Received data for wrong symbol")
                        messageCount++
                        receivedData[symbol]?.set(true)
                        println("Received quote data #$messageCount for $symbol: $message")
                    }
                }

                // Add a delay between each connection
                kotlinx.coroutines.delay(1000)
            }

            // Wait for 11 seconds
            kotlinx.coroutines.delay(11000)

            // Disconnect all
            symbols.forEach { symbol ->
                val params = mapOf("symbols" to symbol)
                quoteSocket.disconnect(params)
            }
        }

        // Verify we received data for all symbols
        receivedData.forEach { (symbol, received) ->
            assertTrue(received.get(), "Should have received data for $symbol")
        }
    }

    @Test
    fun testHoursSocket() = runBlocking {
        val receivedData = AtomicBoolean(false)

        withTimeout(5000) {
            val channel = hoursSocket.connect(Unit)

            val job = launch {
                var messageCount = 0
                for (message in channel) {
                    assertNotNull(message) { "Received null hours data" }
                    messageCount++
                    receivedData.set(true)
                    println("Received hours data #$messageCount: $message")
                }
            }

            // Wait for 3 seconds
            kotlinx.coroutines.delay(3000)
            hoursSocket.disconnect(Unit)
            job.cancel()
        }

        assertTrue(receivedData.get(), "Should have received hours data")
    }
}