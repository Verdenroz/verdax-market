package com.verdenroz.verdaxmarket.core.model

import com.verdenroz.verdaxmarket.core.model.indicators.Adx
import com.verdenroz.verdaxmarket.core.model.indicators.Aroon
import com.verdenroz.verdaxmarket.core.model.indicators.BBands
import com.verdenroz.verdaxmarket.core.model.indicators.Cci
import com.verdenroz.verdaxmarket.core.model.indicators.IchimokuCloud
import com.verdenroz.verdaxmarket.core.model.indicators.Macd
import com.verdenroz.verdaxmarket.core.model.indicators.MovingAverage
import com.verdenroz.verdaxmarket.core.model.indicators.Rsi
import com.verdenroz.verdaxmarket.core.model.indicators.Srsi
import com.verdenroz.verdaxmarket.core.model.indicators.Stoch
import com.verdenroz.verdaxmarket.core.model.indicators.SuperTrend

/**
 * Represents the analysis of a stock that encompasses MAs, trends, and oscillators
 */
data class QuoteAnalysis(
    val sma10: MovingAverage,
    val sma20: MovingAverage,
    val sma50: MovingAverage,
    val sma100: MovingAverage,
    val sma200: MovingAverage,
    val ema10: MovingAverage,
    val ema20: MovingAverage,
    val ema50: MovingAverage,
    val ema100: MovingAverage,
    val ema200: MovingAverage,
    val wma10: MovingAverage,
    val wma20: MovingAverage,
    val wma50: MovingAverage,
    val wma100: MovingAverage,
    val wma200: MovingAverage,
    val vwma20: MovingAverage,
    val rsi14: Rsi,
    val srsi14: Srsi,
    val cci20: Cci,
    val adx14: Adx,
    val macd: Macd,
    val stoch: Stoch,
    val aroon: Aroon,
    val bBands: BBands,
    val superTrend: SuperTrend,
    val ichimokuCloud: IchimokuCloud
)