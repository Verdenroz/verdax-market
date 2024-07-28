package com.verdenroz.verdaxmarket.data.model

import com.verdenroz.verdaxmarket.model.HistoricalData
import com.verdenroz.verdaxmarket.network.model.HistoricalDataResponse

fun HistoricalDataResponse.asExternalModel() = HistoricalData(
    open = open,
    high = high,
    low = low,
    close = close,
    volume = volume,
)

fun Map<String, HistoricalDataResponse>.asExternalModel() = mapValues { it.value.asExternalModel() }