package com.verdenroz.verdaxmarket.core.data.model

import com.verdenroz.verdaxmarket.core.database.model.RecentQuoteEntity
import com.verdenroz.verdaxmarket.core.model.RecentQuoteResult

fun RecentQuoteResult.asEntity() = RecentQuoteEntity(
    symbol = symbol,
    name = name,
    price = price,
    change = change,
    percentChange = percentChange,
    logo = logo,
    timestamp = timestamp
)