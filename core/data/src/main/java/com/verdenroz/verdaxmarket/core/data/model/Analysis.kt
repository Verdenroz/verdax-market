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
import com.verdenroz.verdaxmarket.core.network.model.AdxResponse
import com.verdenroz.verdaxmarket.core.network.model.AnalysisResponse
import com.verdenroz.verdaxmarket.core.network.model.AroonResponse
import com.verdenroz.verdaxmarket.core.network.model.BBandsResponse
import com.verdenroz.verdaxmarket.core.network.model.CciResponse
import com.verdenroz.verdaxmarket.core.network.model.EmaResponse
import com.verdenroz.verdaxmarket.core.network.model.IchimokuCloudResponse
import com.verdenroz.verdaxmarket.core.network.model.MacdResponse
import com.verdenroz.verdaxmarket.core.network.model.RsiResponse
import com.verdenroz.verdaxmarket.core.network.model.SmaResponse
import com.verdenroz.verdaxmarket.core.network.model.StochResponse
import com.verdenroz.verdaxmarket.core.network.model.StochRsiResponse
import com.verdenroz.verdaxmarket.core.network.model.SuperTrendResponse
import com.verdenroz.verdaxmarket.core.network.model.VwmaResponse
import com.verdenroz.verdaxmarket.core.network.model.WmaResponse

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


internal fun SmaResponse.toMA() = MovingAverage(SMA)

internal fun EmaResponse.toMA() = MovingAverage(EMA)

internal fun WmaResponse.toMA() = MovingAverage(WMA)

internal fun VwmaResponse.toMA() = MovingAverage(VWMA)

internal fun RsiResponse.toRsi() = Rsi(RSI)

internal fun StochRsiResponse.toSrsi() = Srsi(k, d)

internal fun CciResponse.toCci() = Cci(CCI)

internal fun AdxResponse.toAdx() = Adx(ADX)

internal fun MacdResponse.toMacd() = Macd(
    macd = macd,
    signal = signal,
)

internal fun StochResponse.toStoch() = Stoch(
    k = k,
    d = d
)

internal fun AroonResponse.toAroon() = Aroon(
    aroonUp = aroonUp,
    aroonDown = aroonDown
)

internal fun BBandsResponse.toBbands() = BBands(
    upperBand = upperBand,
    middleBand = middleBand,
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