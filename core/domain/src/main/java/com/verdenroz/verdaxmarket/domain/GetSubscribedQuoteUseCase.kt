package com.verdenroz.verdaxmarket.domain

import com.verdenroz.verdaxmarket.core.common.dispatchers.Dispatcher
import com.verdenroz.verdaxmarket.core.common.dispatchers.FinanceQueryDispatchers
import com.verdenroz.verdaxmarket.core.common.error.DataError
import com.verdenroz.verdaxmarket.core.common.result.Result
import com.verdenroz.verdaxmarket.core.data.repository.QuoteRepository
import com.verdenroz.verdaxmarket.core.data.utils.MarketMonitor
import com.verdenroz.verdaxmarket.core.data.utils.handleNetworkException
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

    companion object {
        private const val REFRESH_INTERVAL_OPEN = 30000L // 30 seconds
        private const val REFRESH_INTERVAL_CLOSED = 300000L // 5 minutes
    }

    /**
     * Returns a flow of the full quote data for a given symbol that updates
     * every 30 seconds when the market is open and every 5 minutes when the market is closed
     * @param symbol the symbol of the quote to get
     */
    operator fun invoke(symbol: String): Flow<Result<FullQuoteData, DataError.Network>> =
        marketMonitor.isMarketOpen.flatMapLatest { isOpen ->
            flow {
                while (true) {
                    val result = quoteRepository.getFullQuote(symbol).first()
                    emit(result)

                    when (isOpen) {
                        true -> delay(REFRESH_INTERVAL_OPEN)
                        false -> delay(REFRESH_INTERVAL_CLOSED)
                    }
                }
            }
        }.flowOn(ioDispatcher).catch { e -> handleNetworkException(e) }
}