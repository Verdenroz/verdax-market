package com.verdenroz.verdaxmarket.core.data.repository

import com.verdenroz.verdaxmarket.core.common.dispatchers.Dispatcher
import com.verdenroz.verdaxmarket.core.common.dispatchers.FinanceQueryDispatchers
import com.verdenroz.verdaxmarket.core.common.enums.Interval
import com.verdenroz.verdaxmarket.core.common.error.DataError
import com.verdenroz.verdaxmarket.core.common.result.Result
import com.verdenroz.verdaxmarket.core.data.model.asExternalModel
import com.verdenroz.verdaxmarket.core.data.utils.handleNetworkException
import com.verdenroz.verdaxmarket.core.model.AnalysisSignal
import com.verdenroz.verdaxmarket.core.model.QuoteAnalysis
import com.verdenroz.verdaxmarket.core.model.indicators.AnalysisIndicator
import com.verdenroz.verdaxmarket.core.model.indicators.TechnicalIndicator
import com.verdenroz.verdaxmarket.core.network.FinanceQueryDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImplSignalRepository @Inject constructor(
    private val api: FinanceQueryDataSource,
    @Dispatcher(FinanceQueryDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) : SignalRepository {

    override suspend fun getAnalysisMap(symbol: String): Flow<Map<Interval, Result<QuoteAnalysis, DataError.Network>>> =
        flow {
            val analysisMap =
                ConcurrentHashMap<Interval, Result<QuoteAnalysis, DataError.Network>>()

            withContext(ioDispatcher) {
                Interval.entries.map { interval ->
                    async {
                        try {
                            val quoteAnalysis =
                                api.getSummaryAnalysis(symbol, interval).asExternalModel()
                            analysisMap[interval] = Result.Success(quoteAnalysis)
                        } catch (e: Exception) {
                            analysisMap[interval] = Result.Error(handleNetworkException(e))
                        }
                    }
                }
            }.awaitAll()

            emit(analysisMap.toMap())
        }.flowOn(ioDispatcher)

    override suspend fun getIntervalAnalysisSignalMap(
        symbol: String,
        currentPrice: Double
    ): Flow<Map<Interval, Result<Map<TechnicalIndicator, AnalysisSignal>, DataError.Network>>> =
        getAnalysisMap(symbol).flatMapLatest { analysis ->
            flow {
                val intervalAnalysisSignalMap =
                    mutableMapOf<Interval, Result<Map<TechnicalIndicator, AnalysisSignal>, DataError.Network>>()

                withContext(ioDispatcher) {
                    Interval.entries.map { interval ->
                        async {
                            val quoteAnalysis = analysis[interval]
                            val signals = mutableMapOf<TechnicalIndicator, AnalysisSignal>()

                            when (quoteAnalysis) {
                                is Result.Success -> {
                                    val analysisResult = quoteAnalysis.data
                                    analysisResult.let {
                                        // Moving Average Indicators
                                        signals[TechnicalIndicator.SMA10] = createAnalysisSignal(
                                            indicator = it.sma10,
                                            currentPrice = currentPrice
                                        )
                                        signals[TechnicalIndicator.SMA20] = createAnalysisSignal(
                                            indicator = it.sma20,
                                            currentPrice = currentPrice
                                        )
                                        signals[TechnicalIndicator.SMA50] = createAnalysisSignal(
                                            indicator = it.sma50,
                                            currentPrice = currentPrice
                                        )
                                        signals[TechnicalIndicator.SMA100] = createAnalysisSignal(
                                            indicator = it.sma100,
                                            currentPrice = currentPrice
                                        )
                                        signals[TechnicalIndicator.SMA200] = createAnalysisSignal(
                                            indicator = it.sma200,
                                            currentPrice = currentPrice
                                        )
                                        signals[TechnicalIndicator.EMA10] = createAnalysisSignal(
                                            indicator = it.ema10,
                                            currentPrice = currentPrice
                                        )
                                        signals[TechnicalIndicator.EMA20] = createAnalysisSignal(
                                            indicator = it.ema20,
                                            currentPrice = currentPrice
                                        )
                                        signals[TechnicalIndicator.EMA50] = createAnalysisSignal(
                                            indicator = it.ema50,
                                            currentPrice = currentPrice
                                        )
                                        signals[TechnicalIndicator.EMA100] = createAnalysisSignal(
                                            indicator = it.ema100,
                                            currentPrice = currentPrice
                                        )
                                        signals[TechnicalIndicator.EMA200] = createAnalysisSignal(
                                            indicator = it.ema200,
                                            currentPrice = currentPrice
                                        )
                                        signals[TechnicalIndicator.WMA10] = createAnalysisSignal(
                                            indicator = it.wma10,
                                            currentPrice = currentPrice
                                        )
                                        signals[TechnicalIndicator.WMA20] = createAnalysisSignal(
                                            indicator = it.wma20,
                                            currentPrice = currentPrice
                                        )
                                        signals[TechnicalIndicator.WMA50] = createAnalysisSignal(
                                            indicator = it.wma50,
                                            currentPrice = currentPrice
                                        )
                                        signals[TechnicalIndicator.WMA100] = createAnalysisSignal(
                                            indicator = it.wma100,
                                            currentPrice = currentPrice
                                        )
                                        signals[TechnicalIndicator.WMA200] = createAnalysisSignal(
                                            indicator = it.wma200,
                                            currentPrice = currentPrice
                                        )
                                        signals[TechnicalIndicator.VWMA20] = createAnalysisSignal(
                                            indicator = it.vwma20,
                                            currentPrice = currentPrice
                                        )

                                        // Oscillator Indicators
                                        signals[TechnicalIndicator.RSI] = createAnalysisSignal(
                                            indicator = it.rsi14
                                        )
                                        signals[TechnicalIndicator.SRSI] = createAnalysisSignal(
                                            indicator = it.srsi14
                                        )
                                        signals[TechnicalIndicator.CCI] = createAnalysisSignal(
                                            indicator = it.cci20
                                        )
                                        signals[TechnicalIndicator.STOCH] = createAnalysisSignal(
                                            indicator = it.stoch
                                        )

                                        // Trend Indicators
                                        signals[TechnicalIndicator.ADX] = createAnalysisSignal(
                                            indicator = it.adx14
                                        )
                                        signals[TechnicalIndicator.MACD] = createAnalysisSignal(
                                            indicator = it.macd,
                                        )
                                        signals[TechnicalIndicator.BBANDS] = createAnalysisSignal(
                                            indicator = it.bBands,
                                        )
                                        signals[TechnicalIndicator.AROON] = createAnalysisSignal(
                                            indicator = it.aroon,
                                        )
                                        signals[TechnicalIndicator.SUPERTREND] =
                                            createAnalysisSignal(
                                                indicator = it.superTrend,
                                                currentPrice = currentPrice
                                            )
                                        signals[TechnicalIndicator.ICHIMOKUCLOUD] =
                                            createAnalysisSignal(
                                                indicator = it.ichimokuCloud,
                                            )
                                    }
                                    intervalAnalysisSignalMap[interval] = Result.Success(signals.toMap())
                                }

                                // if analysis is not available sets an empty map
                                else -> {
                                    intervalAnalysisSignalMap[interval] = Result.Error(DataError.Network.UNKNOWN)
                                }
                            }
                        }
                    }
                }.awaitAll()

                emit(intervalAnalysisSignalMap.toMap())
            }
        }.flowOn(ioDispatcher)


    /**
     * Create an [AnalysisSignal] based on the [AnalysisIndicator]
     */
    private fun createAnalysisSignal(
        indicator: AnalysisIndicator,
        currentPrice: Double = 0.0,
    ): AnalysisSignal {
        return when (indicator) {
            is AnalysisIndicator.MovingAverageIndicator -> {
                val signal = indicator.toSignal(currentPrice)
                AnalysisSignal.MovingAverageSignal(indicator, signal)
            }

            is AnalysisIndicator.OscillatorIndicator -> {
                val signal = indicator.toSignal()
                AnalysisSignal.OscillatorSignal(indicator, signal)
            }

            is AnalysisIndicator.TrendIndicator -> {
                val signal = indicator.toSignal(currentPrice)
                AnalysisSignal.TrendSignal(indicator, signal)
            }
        }
    }
}