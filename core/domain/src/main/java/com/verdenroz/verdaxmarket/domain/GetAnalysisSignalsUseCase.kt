package com.verdenroz.verdaxmarket.domain

import com.verdenroz.verdaxmarket.core.common.error.DataError
import com.verdenroz.verdaxmarket.core.common.result.Result
import com.verdenroz.verdaxmarket.core.model.AnalysisSignal
import com.verdenroz.verdaxmarket.core.model.FullQuoteData
import com.verdenroz.verdaxmarket.core.model.QuoteAnalysis
import com.verdenroz.verdaxmarket.core.model.indicators.AnalysisIndicator
import com.verdenroz.verdaxmarket.core.model.indicators.TechnicalIndicator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

/**
 * Creates a map of [TechnicalIndicator] to [AnalysisSignal] based on the [FullQuoteData] and [QuoteAnalysis]
 */
class GetAnalysisSignalsUseCase @Inject constructor() {
    operator fun invoke(
        quote: Flow<Result<FullQuoteData, DataError.Network>>,
        analysis: Flow<Result<QuoteAnalysis?, DataError.Network>>
    ): Flow<Map<TechnicalIndicator, AnalysisSignal>> =
        combine(quote, analysis) { quoteData, quoteAnalysis ->
            val signals = mutableMapOf<TechnicalIndicator, AnalysisSignal>()

            if (quoteAnalysis is Result.Success && quoteAnalysis.data != null && quoteData is Result.Success) {
                val analysisResult = quoteAnalysis.data
                val currentPrice = quoteData.data.price

                analysisResult?.let {
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
                    signals[TechnicalIndicator.SUPERTREND] = createAnalysisSignal(
                        indicator = it.superTrend,
                        currentPrice = currentPrice
                    )
                    signals[TechnicalIndicator.ICHIMOKUCLOUD] = createAnalysisSignal(
                        indicator = it.ichimokuCloud,
                    )
                }
            }
            signals.toMap()
        }.catch { emit(emptyMap()) }

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