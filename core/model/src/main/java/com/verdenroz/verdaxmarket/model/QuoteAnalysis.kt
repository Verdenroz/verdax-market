package com.verdenroz.verdaxmarket.model

import com.verdenroz.verdaxmarket.model.indicators.Aroon
import com.verdenroz.verdaxmarket.model.indicators.BBands
import com.verdenroz.verdaxmarket.model.indicators.IchimokuCloud
import com.verdenroz.verdaxmarket.model.indicators.Macd
import com.verdenroz.verdaxmarket.model.indicators.SuperTrend

/**
 * Represents the analysis of a stock that encompasses MAs, trends, and oscillators
 */
data class QuoteAnalysis(
    val sma10: Double,
    val sma20: Double,
    val sma50: Double,
    val sma100: Double,
    val sma200: Double,
    val ema10: Double,
    val ema20: Double,
    val ema50: Double,
    val ema100: Double,
    val ema200: Double,
    val wma10: Double,
    val wma20: Double,
    val wma50: Double,
    val wma100: Double,
    val wma200: Double,
    val vwma20: Double,
    val rsi14: Double,
    val srsi14: Double,
    val cci20: Double,
    val adx14: Double,
    val macd: Macd,
    val stoch: Double,
    val aroon: Aroon,
    val bBands: BBands,
    val superTrend: SuperTrend,
    val ichimokuCloud: IchimokuCloud
)