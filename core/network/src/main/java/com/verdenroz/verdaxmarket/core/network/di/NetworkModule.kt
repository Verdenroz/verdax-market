package com.verdenroz.verdaxmarket.core.network.di

import com.verdenroz.verdaxmarket.core.network.FinanceQueryDataSource
import com.verdenroz.verdaxmarket.core.network.client.ImplFinanceQueryDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.Callback
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import kotlin.coroutines.resumeWithException

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun providesNetworkJson(): Json = Json {
        ignoreUnknownKeys = true
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectionPool(ConnectionPool(5, 5, TimeUnit.MINUTES))
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideFinanceQueryDataSource(
        okHttpClient: OkHttpClient,
        json: Json
    ): FinanceQueryDataSource {
        return ImplFinanceQueryDataSource(
            json,
            okHttpClient,
        )
    }

    /**
     * Extension function to execute a [Call] asynchronously
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun Call.executeAsync(): Response = suspendCancellableCoroutine { continuation ->
        continuation.invokeOnCancellation {
            this.cancel()
        }
        this.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                continuation.resumeWithException(e)
            }

            override fun onResponse(call: Call, response: Response) {
                continuation.resume(value = response, onCancellation = { call.cancel() })
            }
        })
    }
}