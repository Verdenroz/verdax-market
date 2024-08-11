package com.verdenroz.verdaxmarket.core.network.demo

import com.verdenroz.verdaxmarket.core.network.FinanceQueryDataSource
import com.verdenroz.verdaxmarket.core.network.client.ImplFinanceQueryDataSource
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient

class DemoFinanceQueryDataSource {

    operator fun invoke(): FinanceQueryDataSource {
        return ImplFinanceQueryDataSource(
            parser = Json { ignoreUnknownKeys = true },
            client = OkHttpClient.Builder().addInterceptor {
                val request = it.request()
                println(request.url)
                it.proceed(request)
            }.build()
        )
    }
}