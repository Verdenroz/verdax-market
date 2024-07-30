package com.verdenroz.verdaxmarket.core.data.model

import com.verdenroz.verdaxmarket.core.model.QuoteAnalysis
import com.verdenroz.verdaxmarket.core.model.indicators.Aroon
import com.verdenroz.verdaxmarket.core.model.indicators.BBands
import com.verdenroz.verdaxmarket.core.model.indicators.IchimokuCloud
import com.verdenroz.verdaxmarket.core.model.indicators.Macd
import com.verdenroz.verdaxmarket.core.model.indicators.SuperTrend
import com.verdenroz.verdaxmarket.core.network.model.AnalysisResponse
import com.verdenroz.verdaxmarket.core.network.model.indicators.AroonResponse
import com.verdenroz.verdaxmarket.core.network.model.indicators.BBandsResponse
import com.verdenroz.verdaxmarket.core.network.model.indicators.IchimokuCloudResponse
import com.verdenroz.verdaxmarket.core.network.model.indicators.MacdResponse
import com.verdenroz.verdaxmarket.core.network.model.indicators.SuperTrendResponse

fun AnalysisResponse.asExternalModel() = QuoteAnalysis(
    sma10 = sma10,
    sma20 = sma20,
    sma50 = sma50,
    sma100 = sma100,
    sma200 = sma200,
    ema10 = ema10,
    ema20 = ema20,
    ema50 = ema50,
    ema100 = ema100,
    ema200 = ema200,
    wma10 = wma10,
    wma20 = wma20,
    wma50 = wma50,
    wma100 = wma100,
    wma200 = wma200,
    vwma20 = vwma20,
    rsi14 = rsi14,
    srsi14 = srsi14,
    cci20 = cci20,
    adx14 = adx14,
    macd = macd.asExternalModel(),
    stoch = stoch,
    aroon = aroon.asExternalModel(),
    bBands = bBands.asExternalModel(),
    superTrend = superTrend.asExternalModel(),
    ichimokuCloud = ichimokuCloud.asExternalModel()
)

fun MacdResponse.asExternalModel() = Macd(
    macd = macd,
    signal = signal,
)

fun AroonResponse.asExternalModel() = Aroon(
    aroonUp = aroonUp,
    aroonDown = aroonDown
)

fun BBandsResponse.asExternalModel() = BBands(
    upperBand = upperBand,
    lowerBand = lowerBand
)

fun SuperTrendResponse.asExternalModel() = SuperTrend(
    superTrend = superTrend,
    trend = trend,
)

fun IchimokuCloudResponse.asExternalModel() = IchimokuCloud(
    conversionLine = conversionLine,
    baseLine = baseLine,
    laggingSpan = laggingSpan,
    leadingSpanA = leadingSpanA,
    leadingSpanB = leadingSpanB
)