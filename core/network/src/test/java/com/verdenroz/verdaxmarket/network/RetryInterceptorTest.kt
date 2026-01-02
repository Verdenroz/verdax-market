package com.verdenroz.verdaxmarket.network

import com.verdenroz.verdaxmarket.core.network.interceptor.RetryInterceptor
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Unit tests for RetryInterceptor.
 *
 * Tests verify the behavior specified in the prompt:
 * "Add retry on network errors for fetching, using the core common NetworkException only
 * with exponential backoff up to 3 times."
 *
 * F2P Tests: Test new retry functionality at OkHttp interceptor level
 * P2P Tests: Ensure successful requests are not retried
 */
class RetryInterceptorTest {

    private lateinit var retryInterceptor: RetryInterceptor
    private lateinit var testRequest: Request

    @Before
    fun setup() {
        retryInterceptor = RetryInterceptor(
            maxRetries = 3,
            initialDelayMs = 100
        )
        testRequest = Request.Builder()
            .url("https://api.example.com/test")
            .build()
    }

    // P2P: Successful requests should not retry
    @Test
    fun `successful request does not retry`() {
        var attemptCount = 0
        val chain = createTestChain(testRequest) {
            attemptCount++
            createSuccessResponse(testRequest)
        }

        val response = retryInterceptor.intercept(chain)

        assertEquals(200, response.code)
        assertEquals(1, attemptCount)
    }

    // P2P: Other IOExceptions should NOT retry (only UnknownHostException and SocketTimeoutException retry)
    @Test
    fun `does not retry on generic IOException`() {
        var attemptCount = 0
        val chain = createTestChain(testRequest) {
            attemptCount++
            throw IOException("Generic IO error")
        }

        try {
            retryInterceptor.intercept(chain)
            fail("Expected IOException to be thrown immediately")
        } catch (e: IOException) {
            assertEquals("Generic IO error", e.message)
            assertEquals(1, attemptCount) // Should not retry
        }
    }

    // F2P: Should retry on SocketTimeoutException
    @Test
    fun `retries on SocketTimeoutException and eventually succeeds`() {
        var attemptCount = 0
        val chain = createTestChain(testRequest) {
            attemptCount++
            if (attemptCount == 1) {
                throw SocketTimeoutException("Connection timeout")
            }
            createSuccessResponse(testRequest)
        }

        val response = retryInterceptor.intercept(chain)

        assertEquals(200, response.code)
        assertEquals(2, attemptCount) // 1 initial + 1 retry
    }

    // F2P: Should retry on UnknownHostException
    @Test
    fun `retries on UnknownHostException and eventually succeeds`() {
        var attemptCount = 0
        val chain = createTestChain(testRequest) {
            attemptCount++
            if (attemptCount <= 2) {
                throw UnknownHostException("Unknown host")
            }
            createSuccessResponse(testRequest)
        }

        val response = retryInterceptor.intercept(chain)

        assertEquals(200, response.code)
        assertEquals(3, attemptCount) // 1 initial + 2 retries
    }

    // F2P: Should fail after exhausting max retries (3 retries = 4 total attempts)
    @Test
    fun `fails after exhausting max retries on UnknownHostException`() {
        var attemptCount = 0
        val chain = createTestChain(testRequest) {
            attemptCount++
            throw UnknownHostException("Persistent network error")
        }

        try {
            retryInterceptor.intercept(chain)
            fail("Expected UnknownHostException to be thrown after max retries")
        } catch (e: UnknownHostException) {
            assertEquals("Persistent network error", e.message)
            assertEquals(4, attemptCount) // 1 initial + 3 retries
        }
    }

    // F2P: Exponential backoff verification
    @Test
    fun `exponential backoff increases delay between retries`() {
        var attemptCount = 0
        val timestamps = mutableListOf<Long>()
        val chain = createTestChain(testRequest) {
            timestamps.add(System.currentTimeMillis())
            attemptCount++
            throw UnknownHostException("Network error")
        }

        try {
            retryInterceptor.intercept(chain)
        } catch (e: UnknownHostException) {
            // Expected to fail after retries
        }

        // Verify we had 4 attempts
        assertEquals(4, attemptCount)
        assertEquals(4, timestamps.size)

        // Calculate delays between attempts
        val delay1 = timestamps[1] - timestamps[0]
        val delay2 = timestamps[2] - timestamps[1]
        val delay3 = timestamps[3] - timestamps[2]

        // Verify exponential backoff: each delay should be approximately double the previous
        // Using tolerance for timing variations
        assertTrue("First delay should be ~100ms, was ${delay1}ms", delay1 >= 90)
        assertTrue("Second delay should be ~200ms, was ${delay2}ms", delay2 >= 190)
        assertTrue("Third delay should be ~400ms, was ${delay3}ms", delay3 >= 390)

        // Verify delay increases (exponential pattern)
        assertTrue("delay2 ($delay2) should be > delay1 ($delay1)", delay2 > delay1)
        assertTrue("delay3 ($delay3) should be > delay2 ($delay2)", delay3 > delay2)
    }

    // F2P: Custom maxRetries parameter
    @Test
    fun `respects custom maxRetries parameter`() {
        val customRetryInterceptor = RetryInterceptor(maxRetries = 2, initialDelayMs = 50)
        var attemptCount = 0
        val chain = createTestChain(testRequest) {
            attemptCount++
            throw SocketTimeoutException("Network error")
        }

        try {
            customRetryInterceptor.intercept(chain)
            fail("Expected SocketTimeoutException after 2 retries")
        } catch (e: SocketTimeoutException) {
            // With maxRetries = 2, should attempt 3 times (1 initial + 2 retries)
            assertEquals(3, attemptCount)
        }
    }

    // F2P: Custom initialDelayMs parameter
    @Test
    fun `respects custom initialDelayMs parameter`() {
        val customRetryInterceptor = RetryInterceptor(maxRetries = 1, initialDelayMs = 200)
        val timestamps = mutableListOf<Long>()
        val chain = createTestChain(testRequest) {
            timestamps.add(System.currentTimeMillis())
            throw UnknownHostException("Network error")
        }

        try {
            customRetryInterceptor.intercept(chain)
        } catch (e: UnknownHostException) {
            // Expected
        }

        val delay = timestamps[1] - timestamps[0]
        assertTrue("Delay should be ~200ms, was ${delay}ms", delay >= 190)
    }

    // P2P: HTTP errors (4xx, 5xx) should NOT retry (they're not network errors)
    @Test
    fun `does not retry on HTTP 404 error`() {
        var attemptCount = 0
        val chain = createTestChain(testRequest) {
            attemptCount++
            createErrorResponse(testRequest, 404, "Not Found")
        }

        val response = retryInterceptor.intercept(chain)

        assertEquals(404, response.code)
        assertEquals(1, attemptCount) // Should not retry
    }

    // P2P: HTTP errors should NOT retry
    @Test
    fun `does not retry on HTTP 500 error`() {
        var attemptCount = 0
        val chain = createTestChain(testRequest) {
            attemptCount++
            createErrorResponse(testRequest, 500, "Internal Server Error")
        }

        val response = retryInterceptor.intercept(chain)

        assertEquals(500, response.code)
        assertEquals(1, attemptCount) // Should not retry
    }

    // P2P: HTTP errors should NOT retry
    @Test
    fun `does not retry on HTTP 503 error`() {
        var attemptCount = 0
        val chain = createTestChain(testRequest) {
            attemptCount++
            createErrorResponse(testRequest, 503, "Service Unavailable")
        }

        val response = retryInterceptor.intercept(chain)

        assertEquals(503, response.code)
        assertEquals(1, attemptCount) // Should not retry
    }

    // F2P: Multiple retries with different network exception types
    @Test
    fun `handles mixed network exception types correctly`() {
        var attemptCount = 0
        val chain = createTestChain(testRequest) {
            attemptCount++
            when (attemptCount) {
                1 -> throw SocketTimeoutException("Timeout")
                2 -> throw UnknownHostException("Unknown host")
                3 -> throw SocketTimeoutException("Timeout again")
                else -> createSuccessResponse(testRequest)
            }
        }

        val response = retryInterceptor.intercept(chain)

        assertEquals(200, response.code)
        assertEquals(4, attemptCount) // 1 initial + 3 retries
    }

    // F2P: Retry with successful response after few failures
    @Test
    fun `succeeds after one retry`() {
        var attemptCount = 0
        val chain = createTestChain(testRequest) {
            attemptCount++
            if (attemptCount == 1) {
                throw IOException("First attempt failed")
            }
            createSuccessResponse(testRequest)
        }

        val response = retryInterceptor.intercept(chain)

        assertEquals(200, response.code)
        assertEquals(2, attemptCount)
    }

    // F2P: Retry with successful response after few failures
    @Test
    fun `succeeds after two retries`() {
        var attemptCount = 0
        val chain = createTestChain(testRequest) {
            attemptCount++
            if (attemptCount <= 2) {
                throw IOException("Network failure")
            }
            createSuccessResponse(testRequest)
        }

        val response = retryInterceptor.intercept(chain)

        assertEquals(200, response.code)
        assertEquals(3, attemptCount)
    }

    // F2P: Retry with successful response after few failures
    @Test
    fun `succeeds after three retries`() {
        var attemptCount = 0
        val chain = createTestChain(testRequest) {
            attemptCount++
            if (attemptCount <= 3) {
                throw IOException("Network failure")
            }
            createSuccessResponse(testRequest)
        }

        val response = retryInterceptor.intercept(chain)

        assertEquals(200, response.code)
        assertEquals(4, attemptCount)
    }

    // Helper: Create test chain
    private fun createTestChain(
        request: Request,
        proceedBlock: () -> Response
    ): Interceptor.Chain {
        return object : Interceptor.Chain {
            override fun request(): Request = request

            override fun proceed(request: Request): Response = proceedBlock()

            override fun connection(): okhttp3.Connection? = null

            override fun call(): okhttp3.Call {
                throw NotImplementedError("Not needed for test")
            }

            override fun connectTimeoutMillis(): Int = 30000

            override fun withConnectTimeout(timeout: Int, unit: java.util.concurrent.TimeUnit): Interceptor.Chain {
                return this
            }

            override fun readTimeoutMillis(): Int = 30000

            override fun withReadTimeout(timeout: Int, unit: java.util.concurrent.TimeUnit): Interceptor.Chain {
                return this
            }

            override fun writeTimeoutMillis(): Int = 30000

            override fun withWriteTimeout(timeout: Int, unit: java.util.concurrent.TimeUnit): Interceptor.Chain {
                return this
            }
        }
    }

    // Helper: Create successful response
    private fun createSuccessResponse(request: Request): Response {
        return Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .body("{}".toResponseBody("application/json".toMediaType()))
            .build()
    }

    // Helper: Create error response
    private fun createErrorResponse(request: Request, code: Int, message: String): Response {
        return Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(code)
            .message(message)
            .body("{}".toResponseBody("application/json".toMediaType()))
            .build()
    }
}
