package com.verdenroz.verdaxmarket.domain

import com.verdenroz.verdaxmarket.core.common.dispatchers.Dispatcher
import com.verdenroz.verdaxmarket.core.common.dispatchers.FinanceQueryDispatchers
import com.verdenroz.verdaxmarket.core.common.enums.Interval
import com.verdenroz.verdaxmarket.core.common.error.DataError
import com.verdenroz.verdaxmarket.core.common.result.Result
import com.verdenroz.verdaxmarket.core.data.repository.SignalRepository
import com.verdenroz.verdaxmarket.core.model.AnalysisSignal
import com.verdenroz.verdaxmarket.core.model.FullQuoteData
import com.verdenroz.verdaxmarket.core.model.Profile
import com.verdenroz.verdaxmarket.core.model.indicators.TechnicalIndicator
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapNotNull
import javax.inject.Inject

/**
 * A use case which returns all the analysis signals for all available intervals.
 */
class GetAnalysisSignalsUseCase @Inject constructor(
    private val signalRepository: SignalRepository,
    @Dispatcher(FinanceQueryDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) {
    /**
     * Creates a map of [Interval] to another map of [TechnicalIndicator] to [AnalysisSignal]
     * as each indicator has a signal for each interval.
     * @param symbol The symbol of the stock.
     * @param quote The flow of [FullQuoteData] to get current price.
     */
    operator fun invoke(
        symbol: String,
        quote: Flow<Result<Profile, DataError.Network>>
    ): Flow<Map<Interval, Result<Map<TechnicalIndicator, AnalysisSignal>, DataError.Network>>> =
        quote.mapNotNull { result ->
            if (result is Result.Success) result.data.quote.price else null
        }.flatMapLatest { price ->
            signalRepository.getIntervalAnalysisSignalMap(symbol, price)
        }.flowOn(ioDispatcher)

}