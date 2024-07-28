package com.verdenroz.verdaxmarket.data.model

import com.verdenroz.verdaxmarket.model.MarketMover
import com.verdenroz.verdaxmarket.network.model.MarketMoverResponse

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