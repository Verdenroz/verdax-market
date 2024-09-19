package com.verdenroz.verdaxmarket.core.data.model

import com.verdenroz.verdaxmarket.core.database.model.RecentSearchEntity
import com.verdenroz.verdaxmarket.core.model.RecentSearchQuery

fun RecentSearchQuery.asEntity() = RecentSearchEntity(
    query = query,
    timestamp = timestamp
)