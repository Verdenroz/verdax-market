package com.verdenroz.verdaxmarket.core.data.model

import com.verdenroz.verdaxmarket.core.model.MarketIndex
import com.verdenroz.verdaxmarket.core.network.model.IndexResponse

fun List<IndexResponse>.asExternalModel(): List<MarketIndex> {
    return map {
        MarketIndex(
            name = it.name,
            value = it.value.toString(),
            change = it.change,
            percentChange = it.percentChange,
        )
    }
}