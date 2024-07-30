package com.verdenroz.verdaxmarket.core.data.model

import com.verdenroz.verdaxmarket.core.model.MarketMover
import com.verdenroz.verdaxmarket.core.network.model.MarketMoverResponse

fun List<MarketMoverResponse>.asExternalModel(): List<MarketMover> {
    return map {
        MarketMover(
            symbol = it.symbol,
            name = it.name,
            price = it.price,
            change = it.change,
            percentChange = it.percentChange,
        )
    }
}