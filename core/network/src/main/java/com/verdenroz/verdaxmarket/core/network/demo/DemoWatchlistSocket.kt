package com.verdenroz.verdaxmarket.core.network.demo

import com.verdenroz.verdaxmarket.core.network.sockets.WatchlistSocket
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient

class DemoWatchlistSocket {
    operator fun invoke(): WatchlistSocket {
        return WatchlistSocket(
            parser = Json { ignoreUnknownKeys = true },
            client = OkHttpClient.Builder().addInterceptor {
                val request = it.request()
                println(request.url)
                it.proceed(request)
            }.build()
        )
    }
}