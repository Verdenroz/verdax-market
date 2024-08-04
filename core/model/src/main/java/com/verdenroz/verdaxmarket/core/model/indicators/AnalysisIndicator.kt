package com.verdenroz.verdaxmarket.core.model.indicators

import com.verdenroz.verdaxmarket.core.model.QuoteSignal


/**
 * Represents an indicator used in technical analysis with a method to convert the indicator value to a signal
 */
sealed interface AnalysisIndicator {
    sealed interface MovingAverageIndicator : AnalysisIndicator {
        fun toSignal(currentPrice: Double): QuoteSignal
    }

    sealed interface OscillatorIndicator : AnalysisIndicator {
        fun toSignal(): QuoteSignal
    }

    sealed interface TrendIndicator : AnalysisIndicator {
        fun toSignal(currentPrice: Double = 0.0): QuoteSignal
    }
}


/**
 * Represents an indicator used in technical analysis
 */
enum class TechnicalIndicator {
    SMA10,
    SMA20,
    SMA50,
    SMA100,
    SMA200,
    EMA10,
    EMA20,
    EMA50,
    EMA100,
    EMA200,
    WMA10,
    WMA20,
    WMA50,
    WMA100,
    WMA200,
    VWMA20,
    RSI,
    SRSI,
    CCI,
    ADX,
    MACD,
    STOCH,
    AROON,
    BBANDS,
    SUPERTREND,
    ICHIMOKUCLOUD;

    companion object {
        val MOVING_AVERAGES: Set<TechnicalIndicator> = setOf(
            SMA10,
            SMA20,
            SMA50,
            SMA100,
            SMA200,
            EMA10,
            EMA20,
            EMA50,
            EMA100,
            EMA200,
            WMA10,
            WMA20,
            WMA50,
            WMA100,
            WMA200,
            VWMA20
        )
        val OSCILLATORS: Set<TechnicalIndicator> = setOf(
            RSI,
            SRSI,
            STOCH,
            CCI,
        )
        val TRENDS: Set<TechnicalIndicator> = setOf(
            ADX,
            MACD,
            BBANDS,
            AROON,
            SUPERTREND,
            ICHIMOKUCLOUD
        )
    }
}