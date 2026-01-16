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
import com.verdenroz.verdaxmarket.core.network.model.AdxDto
import com.verdenroz.verdaxmarket.core.network.model.AnalysisDto
import com.verdenroz.verdaxmarket.core.network.model.AroonDto
import com.verdenroz.verdaxmarket.core.network.model.BBandsDto
import com.verdenroz.verdaxmarket.core.network.model.CciDto
import com.verdenroz.verdaxmarket.core.network.model.EmaDto
import com.verdenroz.verdaxmarket.core.network.model.IchimokuCloudDto
import com.verdenroz.verdaxmarket.core.network.model.MacdDto
import com.verdenroz.verdaxmarket.core.network.model.RsiDto
import com.verdenroz.verdaxmarket.core.network.model.SmaDto
import com.verdenroz.verdaxmarket.core.network.model.StochDto
import com.verdenroz.verdaxmarket.core.network.model.StochRsiDto
import com.verdenroz.verdaxmarket.core.network.model.SuperTrendDto
import com.verdenroz.verdaxmarket.core.network.model.VwmaDto
import com.verdenroz.verdaxmarket.core.network.model.WmaDto

fun AnalysisDto.asExternalModel() = QuoteAnalysis(
    sma10 = sma10?.toMA() ?: MovingAverage(null),
    sma20 = sma20?.toMA() ?: MovingAverage(null),
    sma50 = sma50?.toMA() ?: MovingAverage(null),
    sma100 = sma100?.toMA() ?: MovingAverage(null),
    sma200 = sma200?.toMA() ?: MovingAverage(null),
    ema10 = ema10?.toMA() ?: MovingAverage(null),
    ema20 = ema20?.toMA() ?: MovingAverage(null),
    ema50 = ema50?.toMA() ?: MovingAverage(null),
    ema100 = ema100?.toMA() ?: MovingAverage(null),
    ema200 = ema200?.toMA() ?: MovingAverage(null),
    wma10 = wma10?.toMA() ?: MovingAverage(null),
    wma20 = wma20?.toMA() ?: MovingAverage(null),
    wma50 = wma50?.toMA() ?: MovingAverage(null),
    wma100 = wma100?.toMA() ?: MovingAverage(null),
    wma200 = wma200?.toMA() ?: MovingAverage(null),
    vwma20 = vwma20?.toMA() ?: MovingAverage(null),
    rsi14 = rsi14?.toRsi() ?: Rsi(null),
    srsi14 = srsi14?.toSrsi() ?: Srsi(null, null),
    cci20 = cci20?.toCci() ?: Cci(null),
    adx14 = adx14?.toAdx() ?: Adx(null),
    macd = macd?.toMacd() ?: Macd(null, null),
    stoch = stoch?.toStoch() ?: Stoch(null, null),
    aroon = aroon?.toAroon() ?: Aroon(null, null),
    bBands = bBands?.toBbands() ?: BBands(null, null, null),
    superTrend = superTrend?.toSupertrend() ?: SuperTrend(null, null),
    ichimokuCloud = ichimokuCloud?.toIchimoku() ?: IchimokuCloud(null, null, null, null, null)
)


internal fun SmaDto.toMA() = MovingAverage(SMA)

internal fun EmaDto.toMA() = MovingAverage(EMA)

internal fun WmaDto.toMA() = MovingAverage(WMA)

internal fun VwmaDto.toMA() = MovingAverage(VWMA)

internal fun RsiDto.toRsi() = Rsi(RSI)

internal fun StochRsiDto.toSrsi() = Srsi(k, d)

internal fun CciDto.toCci() = Cci(CCI)

internal fun AdxDto.toAdx() = Adx(ADX)

internal fun MacdDto.toMacd() = Macd(
    macd = macd,
    signal = signal,
)

internal fun StochDto.toStoch() = Stoch(
    k = k,
    d = d
)

internal fun AroonDto.toAroon() = Aroon(
    aroonUp = aroonUp,
    aroonDown = aroonDown
)

internal fun BBandsDto.toBbands() = BBands(
    upperBand = upperBand,
    middleBand = middleBand,
    lowerBand = lowerBand
)

internal fun SuperTrendDto.toSupertrend() = SuperTrend(
    superTrend = superTrend,
    trend = trend,
)

internal fun IchimokuCloudDto.toIchimoku() = IchimokuCloud(
    conversionLine = conversionLine,
    baseLine = baseLine,
    laggingSpan = laggingSpan,
    leadingSpanA = leadingSpanA,
    leadingSpanB = leadingSpanB
)