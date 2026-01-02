package com.verdenroz.verdaxmarket.core.network.interceptor

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * OkHttp Interceptor that implements retry logic with exponential backoff for network errors.
 *
 * This interceptor automatically retries failed requests that throw network-related exceptions
 * (UnknownHostException, SocketTimeoutException) - these are the exceptions that get wrapped
 * into NetworkException by the data source layer.
 *
 * Retries up to a configurable maximum number of times with exponentially increasing delays.
 *
 * @param maxRetries Maximum number of retry attempts (default: 3)
 * @param initialDelayMs Initial delay in milliseconds before first retry (default: 500ms)
 *
 * Example delays with default settings:
 * - Initial attempt: immediate
 * - 1st retry: after 500ms
 * - 2nd retry: after 1000ms (500ms * 2)
 * - 3rd retry: after 2000ms (1000ms * 2)
 *
 * Only network errors that become core.common.NetworkException are retried:
 * - UnknownHostException (DNS failure, no internet)
 * - SocketTimeoutException (connection timeout)
 *
 * HTTP errors (4xx, 5xx) and other IOExceptions are NOT retried.
 */
class RetryInterceptor(
    private val maxRetries: Int = 3,
    private val initialDelayMs: Long = 500
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var currentDelay = initialDelayMs
        var attempt = 0
        var lastException: IOException? = null

        while (attempt <= maxRetries) {
            try {
                // If this is a retry (not the first attempt), wait with exponential backoff
                if (attempt > 0) {
                    Thread.sleep(currentDelay)
                    currentDelay *= 2 // Double the delay for next retry (exponential backoff)
                }

                // Attempt the request
                return chain.proceed(request)

            } catch (e: UnknownHostException) {
                // Network error - retry
                lastException = e
                attempt++

                if (attempt > maxRetries) {
                    Log.w(
                        "RetryInterceptor",
                        "Request failed after ${attempt} attempts: ${request.url}",
                        e
                    )
                    throw e
                }

                Log.d(
                    "RetryInterceptor",
                    "Retry attempt $attempt/$maxRetries for ${request.url} after UnknownHostException"
                )

            } catch (e: SocketTimeoutException) {
                // Network error - retry
                lastException = e
                attempt++

                if (attempt > maxRetries) {
                    Log.w(
                        "RetryInterceptor",
                        "Request failed after ${attempt} attempts: ${request.url}",
                        e
                    )
                    throw e
                }

                Log.d(
                    "RetryInterceptor",
                    "Retry attempt $attempt/$maxRetries for ${request.url} after SocketTimeoutException"
                )

            } catch (e: IOException) {
                // Other IOException (not network related) - don't retry, throw immediately
                Log.d(
                    "RetryInterceptor",
                    "Non-network IOException, not retrying: ${e.javaClass.simpleName}"
                )
                throw e
            }
        }

        // This should never be reached due to the throws above, but needed for compilation
        throw lastException ?: IOException("Unknown error during retry")
    }
}
