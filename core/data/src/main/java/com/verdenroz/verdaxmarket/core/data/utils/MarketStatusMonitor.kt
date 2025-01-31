package com.verdenroz.verdaxmarket.core.data.utils

import com.verdenroz.verdaxmarket.core.common.dispatchers.Dispatcher
import com.verdenroz.verdaxmarket.core.common.dispatchers.FinanceQueryDispatchers
import com.verdenroz.verdaxmarket.core.model.MarketHours
import com.verdenroz.verdaxmarket.core.model.enums.MarketStatus
import com.verdenroz.verdaxmarket.core.model.enums.MarketStatusReason
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.time.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MarketStatusMonitor @Inject constructor(
    @Dispatcher(FinanceQueryDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) : MarketMonitor {

    companion object {
        private val REGULAR_OPEN = LocalTime.of(9, 30)
        private val REGULAR_CLOSE = LocalTime.of(16, 0)
        private val EARLY_CLOSE_TIME = LocalTime.of(13, 0)
        private val PRE_MARKET_START = LocalTime.of(4, 0)
        private val AFTER_HOURS_END = LocalTime.of(20, 0)
    }

    override val marketHours: Flow<MarketHours> = flow {
        while (true) {
            emit(getCurrentMarketStatus())
            delay(10000L)
        }
    }.flowOn(ioDispatcher)

    private fun getCurrentMarketStatus(): MarketHours {
        val nyZone = ZoneId.of("America/New_York")
        val nowNY = ZonedDateTime.now(nyZone)
        val currentDate = nowNY.toLocalDate()
        val currentTime = nowNY.toLocalTime()

        // Check weekend
        if (currentDate.dayOfWeek in listOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)) {
            return MarketHours(MarketStatus.CLOSED, MarketStatusReason.WEEKEND)
        }

        // Check holidays
        if (isMarketHoliday(currentDate)) {
            return MarketHours(MarketStatus.CLOSED, MarketStatusReason.HOLIDAY)
        }

        // Check early close days
        if (isEarlyCloseDay(currentDate)) {
            return when {
                currentTime < REGULAR_OPEN ->
                    MarketHours(MarketStatus.PREMARKET, MarketStatusReason.PRE_MARKET)
                currentTime >= EARLY_CLOSE_TIME ->
                    MarketHours(MarketStatus.CLOSED, MarketStatusReason.EARLY_CLOSE)
                else ->
                    MarketHours(MarketStatus.EARLY_CLOSE, MarketStatusReason.EARLY_CLOSE)
            }
        }

        // Regular trading day logic
        return when {
            currentTime < REGULAR_OPEN && currentTime >= PRE_MARKET_START ->
                MarketHours(MarketStatus.PREMARKET, MarketStatusReason.PRE_MARKET)
            currentTime < PRE_MARKET_START || currentTime >= AFTER_HOURS_END ->
                MarketHours(MarketStatus.CLOSED, MarketStatusReason.OUTSIDE_HOURS)
            currentTime >= REGULAR_CLOSE && currentTime < AFTER_HOURS_END ->
                MarketHours(MarketStatus.AFTER_HOURS, MarketStatusReason.AFTER_HOURS)
            currentTime >= REGULAR_OPEN && currentTime < REGULAR_CLOSE ->
                MarketHours(MarketStatus.OPEN, MarketStatusReason.REGULAR_HOURS)
            else ->
                MarketHours(MarketStatus.CLOSED, MarketStatusReason.OUTSIDE_HOURS)
        }
    }
}