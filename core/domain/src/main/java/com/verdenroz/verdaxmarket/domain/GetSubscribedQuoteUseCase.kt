package com.verdenroz.verdaxmarket.domain

import com.verdenroz.verdaxmarket.core.common.dispatchers.Dispatcher
import com.verdenroz.verdaxmarket.core.common.dispatchers.FinanceQueryDispatchers
import com.verdenroz.verdaxmarket.core.common.error.DataError
import com.verdenroz.verdaxmarket.core.common.result.Result
import com.verdenroz.verdaxmarket.core.data.repository.QuoteRepository
import com.verdenroz.verdaxmarket.core.data.utils.MarketMonitor
import com.verdenroz.verdaxmarket.core.model.FullQuoteData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

/**
 * A use case that gets the subscribed quote for a given symbol
 */
class GetSubscribedQuoteUseCase @Inject constructor(
    private val quoteRepository: QuoteRepository,
    private val marketMonitor: MarketMonitor,
    @Dispatcher(FinanceQueryDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) {
    /**
     * Returns a flow of the full quote data for a given symbol that updates
     * every 10 seconds when the market is open and every 60 seconds when the market is closed
     * @param symbol the symbol of the quote to get
     */
    operator fun invoke(symbol: String): Flow<Result<FullQuoteData, DataError.Network>> =
        marketMonitor.isMarketOpen.flatMapLatest { isOpen ->
            flow {
                while (true) {
                    val result = quoteRepository.getFullQuote(symbol).first()
                    emit(result)
                    val refreshInterval = if (isOpen) 10000L else 60000L
                    delay(refreshInterval)
                }
            }
        }.flowOn(ioDispatcher).catch { emit(Result.Error(DataError.Network.UNKNOWN)) }

}