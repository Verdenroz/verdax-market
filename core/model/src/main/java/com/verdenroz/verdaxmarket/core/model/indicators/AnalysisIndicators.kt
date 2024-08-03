package com.verdenroz.verdaxmarket.core.model.indicators

sealed interface AnalysisIndicator {

    sealed interface MovingAverageIndicator : AnalysisIndicator {
        data object SMA10 : MovingAverageIndicator
        data object SMA20 : MovingAverageIndicator
        data object SMA50 : MovingAverageIndicator
        data object SMA100 : MovingAverageIndicator
        data object SMA200 : MovingAverageIndicator
        data object EMA10 : MovingAverageIndicator
        data object EMA20 : MovingAverageIndicator
        data object EMA50 : MovingAverageIndicator
        data object EMA100 : MovingAverageIndicator
        data object EMA200 : MovingAverageIndicator
        data object WMA10 : MovingAverageIndicator
        data object WMA20 : MovingAverageIndicator
        data object WMA50 : MovingAverageIndicator
        data object WMA100 : MovingAverageIndicator
        data object WMA200 : MovingAverageIndicator
        data object VWMA20 : MovingAverageIndicator
    }

    sealed interface OscillatorIndicator : AnalysisIndicator {
        data object RSI : OscillatorIndicator
        data object SRSI : OscillatorIndicator
        data object CCI : OscillatorIndicator
        data object ADX : OscillatorIndicator
        data object MACD : OscillatorIndicator
        data object STOCH : OscillatorIndicator
    }

    sealed interface TrendIndicator : AnalysisIndicator {
        data object AROON : TrendIndicator
        data object BBANDS : TrendIndicator
        data object SUPERTREND : TrendIndicator
        data object ICHIMOKUCLOUD : TrendIndicator
    }

    companion object {
        val MOVING_AVERAGES: Set<MovingAverageIndicator> = setOf(
            MovingAverageIndicator.SMA10,
            MovingAverageIndicator.SMA20,
            MovingAverageIndicator.SMA50,
            MovingAverageIndicator.SMA100,
            MovingAverageIndicator.SMA200,
            MovingAverageIndicator.EMA10,
            MovingAverageIndicator.EMA20,
            MovingAverageIndicator.EMA50,
            MovingAverageIndicator.EMA100,
            MovingAverageIndicator.EMA200,
            MovingAverageIndicator.WMA10,
            MovingAverageIndicator.WMA20,
            MovingAverageIndicator.WMA50,
            MovingAverageIndicator.WMA100,
            MovingAverageIndicator.WMA200,
            MovingAverageIndicator.VWMA20
        )
        val OSCILLATORS: Set<OscillatorIndicator> = setOf(
            OscillatorIndicator.RSI,
            OscillatorIndicator.SRSI,
            OscillatorIndicator.STOCH,
            OscillatorIndicator.CCI,
            OscillatorIndicator.ADX,
            OscillatorIndicator.MACD,
        )
        val TRENDS: Set<TrendIndicator> = setOf(
            TrendIndicator.BBANDS,
            TrendIndicator.AROON,
            TrendIndicator.SUPERTREND,
            TrendIndicator.ICHIMOKUCLOUD
        )
    }
}
