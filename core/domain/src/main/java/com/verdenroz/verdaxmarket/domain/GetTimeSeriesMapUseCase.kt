package com.verdenroz.verdaxmarket.domain

import android.util.Log
import com.verdenroz.verdaxmarket.core.common.dispatchers.Dispatcher
import com.verdenroz.verdaxmarket.core.common.dispatchers.FinanceQueryDispatchers
import com.verdenroz.verdaxmarket.core.common.enums.Interval
import com.verdenroz.verdaxmarket.core.common.enums.TimePeriod
import com.verdenroz.verdaxmarket.core.common.error.DataError
import com.verdenroz.verdaxmarket.core.common.result.Result
import com.verdenroz.verdaxmarket.core.data.model.asExternalModel
import com.verdenroz.verdaxmarket.core.data.utils.handleNetworkException
import com.verdenroz.verdaxmarket.core.model.HistoricalData
import com.verdenroz.verdaxmarket.core.network.FinanceQueryDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

/**
 * Use case to get historical data for a symbol for different time periods.
 */
class GetTimeSeriesMapUseCase @Inject constructor(
    private val api: FinanceQueryDataSource,
    @Dispatcher(FinanceQueryDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) {

    operator fun invoke(symbol: String): Flow<Map<TimePeriod, Result<Map<String, HistoricalData>, DataError.Network>>> =
        flow {
            val timeSeriesMap =
                ConcurrentHashMap<TimePeriod, Result<Map<String, HistoricalData>, DataError.Network>>()

            withContext(ioDispatcher) {
                TimePeriod.entries.map { timePeriod ->
                    async {
                        try {
                            val interval = timeToInterval(timePeriod)
                            val historicalData = api.getHistoricalData(symbol, timePeriod, interval).asExternalModel()
                            Log.d("GetTimeSeriesMapUseCase", "Historical data for $interval: $historicalData")
                            timeSeriesMap[timePeriod] = Result.Success(historicalData)
                        } catch (e: Exception) {
                            timeSeriesMap[timePeriod] = handleNetworkException(e)
                        }
                    }
                }
            }.awaitAll()

            emit(timeSeriesMap.toMap())
        }.flowOn(ioDispatcher)

    private fun timeToInterval(timePeriod: TimePeriod): Interval = when (timePeriod) {
        TimePeriod.ONE_DAY -> Interval.ONE_MINUTE
        TimePeriod.FIVE_DAY -> Interval.FIVE_MINUTE
        else -> Interval.DAILY
    }
}