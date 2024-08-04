package com.verdenroz.verdaxmarket.core.data.model

import com.verdenroz.verdaxmarket.core.model.QuoteAnalysis
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
import com.verdenroz.verdaxmarket.core.network.model.AnalysisResponse
import com.verdenroz.verdaxmarket.core.network.model.indicators.AroonResponse
import com.verdenroz.verdaxmarket.core.network.model.indicators.BBandsResponse
import com.verdenroz.verdaxmarket.core.network.model.indicators.IchimokuCloudResponse
import com.verdenroz.verdaxmarket.core.network.model.indicators.MacdResponse
import com.verdenroz.verdaxmarket.core.network.model.indicators.SuperTrendResponse

fun AnalysisResponse.asExternalModel() = QuoteAnalysis(
    sma10 = sma10.toMA(),
    sma20 = sma20.toMA(),
    sma50 = sma50.toMA(),
    sma100 = sma100.toMA(),
    sma200 = sma200.toMA(),
    ema10 = ema10.toMA(),
    ema20 = ema20.toMA(),
    ema50 = ema50.toMA(),
    ema100 = ema100.toMA(),
    ema200 = ema200.toMA(),
    wma10 = wma10.toMA(),
    wma20 = wma20.toMA(),
    wma50 = wma50.toMA(),
    wma100 = wma100.toMA(),
    wma200 = wma200.toMA(),
    vwma20 = vwma20.toMA(),
    rsi14 = rsi14.toRsi(),
    srsi14 = srsi14.toSrsi(),
    cci20 = cci20.toCci(),
    adx14 = adx14.toAdx(),
    macd = macd.toMacd(),
    stoch = stoch.toStoch(),
    aroon = aroon.toAroon(),
    bBands = bBands.toBbands(),
    superTrend = superTrend.toSupertrend(),
    ichimokuCloud = ichimokuCloud.toIchimoku()
)

internal fun Double.toMA() = MovingAverage(this)

internal fun Double.toRsi() = Rsi(this)

internal fun Double.toSrsi() = Srsi(this)

internal fun Double.toCci() = Cci(this)

internal fun Double.toAdx() = Adx(this)

internal fun Double.toStoch() = Stoch(this)

internal fun MacdResponse.toMacd() = Macd(
    macd = macd,
    signal = signal,
)

internal fun AroonResponse.toAroon() = Aroon(
    aroonUp = aroonUp,
    aroonDown = aroonDown
)

internal fun BBandsResponse.toBbands() = BBands(
    upperBand = upperBand,
    lowerBand = lowerBand
)

internal fun SuperTrendResponse.toSupertrend() = SuperTrend(
    superTrend = superTrend,
    trend = trend,
)

internal fun IchimokuCloudResponse.toIchimoku() = IchimokuCloud(
    conversionLine = conversionLine,
    baseLine = baseLine,
    laggingSpan = laggingSpan,
    leadingSpanA = leadingSpanA,
    leadingSpanB = leadingSpanB
)