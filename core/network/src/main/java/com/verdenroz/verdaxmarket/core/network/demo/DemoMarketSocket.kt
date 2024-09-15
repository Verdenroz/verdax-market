package com.verdenroz.verdaxmarket.core.network.demo

import com.verdenroz.verdaxmarket.core.network.sockets.MarketSocket
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient

class DemoMarketSocket {
    operator fun invoke(): MarketSocket {
        return MarketSocket(
            parser = Json { ignoreUnknownKeys = true },
            client = OkHttpClient.Builder().addInterceptor {
                val request = it.request()
                println(request.url)
                it.proceed(request)
            }.build()
        )
    }
}