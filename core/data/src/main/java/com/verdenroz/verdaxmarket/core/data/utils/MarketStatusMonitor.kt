package com.verdenroz.verdaxmarket.core.data.utils

import com.verdenroz.verdaxmarket.core.common.dispatchers.Dispatcher
import com.verdenroz.verdaxmarket.core.common.dispatchers.FinanceQueryDispatchers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject

class MarketStatusMonitor @Inject constructor(
    @Dispatcher(FinanceQueryDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
): MarketMonitor {
    override val isMarketOpen: Flow<Boolean> = flow {
        while (true) {
            emit(isMarketOpen())
            delay(10000L)
        }
    }.flowOn(ioDispatcher)

    /**
     * Check if the market is currently open
     * TODO: Account for holidays
     */
    internal fun isMarketOpen(): Boolean {
        val now = LocalTime.now(ZoneId.systemDefault())
        val nowNewYork = now.atDate(LocalDate.now())
            .atZone(ZoneId.systemDefault())
            .withZoneSameInstant(ZoneId.of("America/New_York"))

        val isWeekday = nowNewYork.dayOfWeek !in listOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
        val isBusinessHours =
            nowNewYork.toLocalTime().isAfter(LocalTime.of(9, 30)) && nowNewYork.toLocalTime()
                .isBefore(LocalTime.of(16, 0))

        return isWeekday && isBusinessHours
    }
}