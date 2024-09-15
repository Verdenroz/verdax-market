package com.verdenroz.verdaxmarket.core.network.demo

import com.verdenroz.verdaxmarket.core.network.sockets.ProfileSocket
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient

class DemoProfileSocket {
    operator fun invoke(): ProfileSocket {
        return ProfileSocket(
            parser = Json { ignoreUnknownKeys = true },
            client = OkHttpClient.Builder().addInterceptor {
                val request = it.request()
                println(request.url)
                it.proceed(request)
            }.build()
        )
    }
}