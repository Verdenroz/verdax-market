package com.verdenroz.verdaxmarket.core.data.model

import com.verdenroz.verdaxmarket.core.model.MarketIndex
import com.verdenroz.verdaxmarket.core.network.model.IndexDto

fun List<IndexDto>.asExternalModel(): List<MarketIndex> {
    return map {
        MarketIndex(
            name = it.name,
            value = it.value.toString(),
            change = it.change,
            percentChange = it.percentChange,
            fiveDaysReturn = it.fiveDaysReturn,
            oneMonthReturn = it.oneMonthReturn,
            sixMonthReturn = it.sixMonthReturn,
            ytdReturn = it.ytdReturn,
            yearReturn = it.yearReturn,
            fiveYearReturn = it.fiveYearReturn,
        )
    }
}