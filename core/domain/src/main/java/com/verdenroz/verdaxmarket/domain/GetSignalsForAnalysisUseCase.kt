package com.verdenroz.verdaxmarket.domain

import com.verdenroz.verdaxmarket.core.common.error.DataError
import com.verdenroz.verdaxmarket.core.common.result.Result
import com.verdenroz.verdaxmarket.core.model.FullQuoteData
import com.verdenroz.verdaxmarket.core.model.QuoteAnalysis
import com.verdenroz.verdaxmarket.core.model.QuoteSignal
import com.verdenroz.verdaxmarket.core.model.indicators.AnalysisIndicator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

/**
 * Use case that maps the [AnalysisIndicator] to signals as either [QuoteSignal.BUY], [QuoteSignal.SELL] or [QuoteSignal.NEUTRAL]
 */
class GetSignalsForAnalysisUseCase @Inject constructor() {
    operator fun invoke(
        quote: Flow<Result<FullQuoteData, DataError.Network>>,
        analysis: Flow<Result<QuoteAnalysis?, DataError.Network>>
    ): Flow<Map<AnalysisIndicator, QuoteSignal>> = combine(quote, analysis) { quoteData, quoteAnalysis ->
        val signals = mutableMapOf<AnalysisIndicator, QuoteSignal>()

        if (quoteAnalysis is Result.Success && quoteAnalysis.data != null && quoteData is Result.Success) {
            val analysisResult = quoteAnalysis.data
            val currentPrice = quoteData.data.price
            analysisResult?.let {
                signals[AnalysisIndicator.MovingAverageIndicator.SMA10] = createSignal(it.sma10, AnalysisIndicator.MovingAverageIndicator.SMA10, currentPrice)
                signals[AnalysisIndicator.MovingAverageIndicator.SMA20] = createSignal(it.sma20, AnalysisIndicator.MovingAverageIndicator.SMA20, currentPrice)
                signals[AnalysisIndicator.MovingAverageIndicator.SMA50] = createSignal(it.sma50, AnalysisIndicator.MovingAverageIndicator.SMA50, currentPrice)
                signals[AnalysisIndicator.MovingAverageIndicator.SMA100] = createSignal(it.sma100, AnalysisIndicator.MovingAverageIndicator.SMA100, currentPrice)
                signals[AnalysisIndicator.MovingAverageIndicator.SMA200] = createSignal(it.sma200, AnalysisIndicator.MovingAverageIndicator.SMA200, currentPrice)
                signals[AnalysisIndicator.MovingAverageIndicator.EMA10] = createSignal(it.ema10, AnalysisIndicator.MovingAverageIndicator.EMA10, currentPrice)
                signals[AnalysisIndicator.MovingAverageIndicator.EMA20] = createSignal(it.ema20, AnalysisIndicator.MovingAverageIndicator.EMA20, currentPrice)
                signals[AnalysisIndicator.MovingAverageIndicator.EMA50] = createSignal(it.ema50, AnalysisIndicator.MovingAverageIndicator.EMA50, currentPrice)
                signals[AnalysisIndicator.MovingAverageIndicator.EMA100] = createSignal(it.ema100, AnalysisIndicator.MovingAverageIndicator.EMA100, currentPrice)
                signals[AnalysisIndicator.MovingAverageIndicator.EMA200] = createSignal(it.ema200, AnalysisIndicator.MovingAverageIndicator.EMA200, currentPrice)
                signals[AnalysisIndicator.MovingAverageIndicator.WMA10] = createSignal(it.wma10, AnalysisIndicator.MovingAverageIndicator.WMA10, currentPrice)
                signals[AnalysisIndicator.MovingAverageIndicator.WMA20] = createSignal(it.wma20, AnalysisIndicator.MovingAverageIndicator.WMA20, currentPrice)
                signals[AnalysisIndicator.MovingAverageIndicator.WMA50] = createSignal(it.wma50, AnalysisIndicator.MovingAverageIndicator.WMA50, currentPrice)
                signals[AnalysisIndicator.MovingAverageIndicator.WMA100] = createSignal(it.wma100, AnalysisIndicator.MovingAverageIndicator.WMA100, currentPrice)
                signals[AnalysisIndicator.MovingAverageIndicator.WMA200] = createSignal(it.wma200, AnalysisIndicator.MovingAverageIndicator.WMA200, currentPrice)
                signals[AnalysisIndicator.MovingAverageIndicator.VWMA20] = createSignal(it.vwma20, AnalysisIndicator.MovingAverageIndicator.VWMA20, currentPrice)
                signals[AnalysisIndicator.OscillatorIndicator.RSI] = createSignal(it.rsi14, AnalysisIndicator.OscillatorIndicator.RSI, currentPrice)
                signals[AnalysisIndicator.OscillatorIndicator.SRSI] = createSignal(it.srsi14, AnalysisIndicator.OscillatorIndicator.SRSI, currentPrice)
                signals[AnalysisIndicator.OscillatorIndicator.CCI] = createSignal(it.cci20, AnalysisIndicator.OscillatorIndicator.CCI, currentPrice)
                signals[AnalysisIndicator.OscillatorIndicator.ADX] = createSignal(it.adx14, AnalysisIndicator.OscillatorIndicator.ADX, currentPrice)
                signals[AnalysisIndicator.OscillatorIndicator.MACD] = createSignal(it.macd.macd, AnalysisIndicator.OscillatorIndicator.MACD, currentPrice, it.macd.signal)
                signals[AnalysisIndicator.OscillatorIndicator.STOCH] = createSignal(it.stoch, AnalysisIndicator.OscillatorIndicator.STOCH, currentPrice)
                signals[AnalysisIndicator.TrendIndicator.AROON] = createSignal(it.aroon.aroonUp, AnalysisIndicator.TrendIndicator.AROON, currentPrice, it.aroon.aroonDown)
                signals[AnalysisIndicator.TrendIndicator.BBANDS] = createSignal(it.bBands.upperBand, AnalysisIndicator.TrendIndicator.BBANDS, currentPrice, it.bBands.lowerBand)
                signals[AnalysisIndicator.TrendIndicator.SUPERTREND] = createSignal(it.superTrend.superTrend, AnalysisIndicator.TrendIndicator.SUPERTREND, currentPrice)
                signals[AnalysisIndicator.TrendIndicator.ICHIMOKUCLOUD] = createSignal(it.ichimokuCloud.leadingSpanA, AnalysisIndicator.TrendIndicator.ICHIMOKUCLOUD, currentPrice, it.ichimokuCloud.leadingSpanB)
            }
        }
        signals.toMap()
    }

    private fun createSignal(
        indicatorValue: Double?,
        indicator: AnalysisIndicator,
        currentPrice: Double = 0.0,
        base: Double? = 0.0,
    ): QuoteSignal {
        if (indicatorValue == null || base == null) return QuoteSignal.NEUTRAL

        return when (indicator) {
            is AnalysisIndicator.MovingAverageIndicator -> {
                when {
                    indicatorValue < currentPrice -> QuoteSignal.BUY
                    indicatorValue > currentPrice -> QuoteSignal.SELL
                    else -> QuoteSignal.NEUTRAL
                }
            }

            is AnalysisIndicator.OscillatorIndicator -> {
                when (indicator) {
                    AnalysisIndicator.OscillatorIndicator.RSI -> {
                        when {
                            indicatorValue < 30 -> QuoteSignal.BUY
                            indicatorValue > 70 -> QuoteSignal.SELL
                            else -> QuoteSignal.NEUTRAL
                        }
                    }

                    AnalysisIndicator.OscillatorIndicator.SRSI,
                    AnalysisIndicator.OscillatorIndicator.STOCH -> {
                        when {
                            indicatorValue < 20 -> QuoteSignal.BUY
                            indicatorValue > 80 -> QuoteSignal.SELL
                            else -> QuoteSignal.NEUTRAL
                        }
                    }

                    AnalysisIndicator.OscillatorIndicator.CCI -> {
                        when {
                            indicatorValue < -100 -> QuoteSignal.BUY
                            indicatorValue > 100 -> QuoteSignal.SELL
                            else -> QuoteSignal.NEUTRAL
                        }
                    }

                    AnalysisIndicator.OscillatorIndicator.ADX -> QuoteSignal.NEUTRAL

                    AnalysisIndicator.OscillatorIndicator.MACD -> {
                        when {
                            indicatorValue > base -> QuoteSignal.BUY
                            else -> QuoteSignal.SELL
                        }
                    }
                }
            }

            is AnalysisIndicator.TrendIndicator -> {
                when (indicator) {
                    AnalysisIndicator.TrendIndicator.AROON,
                    AnalysisIndicator.TrendIndicator.ICHIMOKUCLOUD -> {
                        when {
                            indicatorValue > base -> QuoteSignal.BUY
                            else -> QuoteSignal.SELL
                        }
                    }

                    AnalysisIndicator.TrendIndicator.BBANDS -> {
                        when {
                            currentPrice > indicatorValue -> QuoteSignal.BUY
                            currentPrice < base -> QuoteSignal.SELL
                            else -> QuoteSignal.NEUTRAL
                        }
                    }

                    AnalysisIndicator.TrendIndicator.SUPERTREND -> {
                        when {
                            currentPrice > indicatorValue -> QuoteSignal.BUY
                            else -> QuoteSignal.SELL
                        }
                    }
                }
            }
        }
    }
}