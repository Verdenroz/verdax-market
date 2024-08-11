package com.verdenroz.verdaxmarket.data

import com.verdenroz.verdaxmarket.core.common.enums.Interval
import com.verdenroz.verdaxmarket.core.common.enums.TimePeriod
import com.verdenroz.verdaxmarket.core.data.model.asExternalModel
import com.verdenroz.verdaxmarket.core.network.FinanceQueryDataSource
import com.verdenroz.verdaxmarket.core.network.demo.DemoFinanceQueryDataSource
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class DataFormatTests {

    lateinit var api: FinanceQueryDataSource

    @Before
    fun setup() {
        api = DemoFinanceQueryDataSource().invoke()
    }

    @Test
    fun getFormattedTimeSeries() {
        runBlocking {
            val unformattedResult = api.getHistoricalData("AAPL", time = TimePeriod.YEAR_TO_DATE, interval = Interval.DAILY)
            val formattedResult = unformattedResult.asExternalModel()

            println("Unformatted Size: ${unformattedResult.size} ------  Formatted Size: ${formattedResult.size}")
            println(formattedResult)

            assert(formattedResult.isNotEmpty())
            assert(formattedResult.size == unformattedResult.size)
        }
    }
}