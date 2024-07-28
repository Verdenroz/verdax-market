package com.verdenroz.verdaxmarket.data.model

import com.verdenroz.verdaxmarket.model.MarketIndex
import com.verdenroz.verdaxmarket.network.model.IndexResponse

fun List<IndexResponse>.asExternalModel(): List<MarketIndex> {
    return map {
        MarketIndex(
            name = it.name,
            value = it.value,
            change = it.change,
            percentChange = it.percentChange,
        )
    }
}