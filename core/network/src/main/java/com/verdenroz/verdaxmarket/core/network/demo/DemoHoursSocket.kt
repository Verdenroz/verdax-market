package com.verdenroz.verdaxmarket.core.network.demo

import com.verdenroz.verdaxmarket.core.network.sockets.HoursSocket
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient

class DemoHoursSocket {
    operator fun invoke(): HoursSocket {
        return HoursSocket(
            parser = Json { ignoreUnknownKeys = true },
            client = OkHttpClient.Builder().addInterceptor {
                val request = it.request()
                println(request.url)
                it.proceed(request)
            }.build()
        )
    }
}