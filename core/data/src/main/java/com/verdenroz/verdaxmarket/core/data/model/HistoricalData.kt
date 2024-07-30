package com.verdenroz.verdaxmarket.core.data.model

import com.verdenroz.verdaxmarket.core.model.HistoricalData
import com.verdenroz.verdaxmarket.core.network.model.HistoricalDataResponse

fun HistoricalDataResponse.asExternalModel() = HistoricalData(
    open = open,
    high = high,
    low = low,
    close = close,
    volume = volume,
)

fun Map<String, HistoricalDataResponse>.asExternalModel() = mapValues { it.value.asExternalModel() }