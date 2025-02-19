package com.verdenroz.verdaxmarket.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AnalysisResponse(
    @SerialName("SMA(10)")
    val sma10: SmaResponse,
    @SerialName("SMA(20)")
    val sma20: SmaResponse,
    @SerialName("SMA(50)")
    val sma50: SmaResponse,
    @SerialName("SMA(100)")
    val sma100: SmaResponse,
    @SerialName("SMA(200)")
    val sma200: SmaResponse,
    @SerialName("EMA(10)")
    val ema10: EmaResponse,
    @SerialName("EMA(20)")
    val ema20: EmaResponse,
    @SerialName("EMA(50)")
    val ema50: EmaResponse,
    @SerialName("EMA(100)")
    val ema100: EmaResponse,
    @SerialName("EMA(200)")
    val ema200: EmaResponse,
    @SerialName("WMA(10)")
    val wma10: WmaResponse,
    @SerialName("WMA(20)")
    val wma20: WmaResponse,
    @SerialName("WMA(50)")
    val wma50: WmaResponse,
    @SerialName("WMA(100)")
    val wma100: WmaResponse,
    @SerialName("WMA(200)")
    val wma200: WmaResponse,
    @SerialName("VWMA(20)")
    val vwma20: VwmaResponse,
    @SerialName("RSI(14)")
    val rsi14: RsiResponse,
    @SerialName("SRSI(3,3,14,14)")
    val srsi14: StochRsiResponse,
    @SerialName("CCI(20)")
    val cci20: CciResponse,
    @SerialName("ADX(14)")
    val adx14: AdxResponse,
    @SerialName("MACD(12,26)")
    val macd: MacdResponse,
    @SerialName("STOCH %K(14,3,3)")
    val stoch: StochResponse,
    @SerialName("Aroon(25)")
    val aroon: AroonResponse,
    @SerialName("BBANDS(20,2)")
    val bBands: BBandsResponse,
    @SerialName("Super Trend")
    val superTrend: SuperTrendResponse,
    @SerialName("Ichimoku Cloud")
    val ichimokuCloud: IchimokuCloudResponse
)

@Serializable
data class SmaResponse(val SMA: Double?)

@Serializable
data class EmaResponse(val EMA: Double?)

@Serializable
data class WmaResponse(val WMA: Double?)

@Serializable
data class VwmaResponse(val VWMA: Double?)

@Serializable
data class RsiResponse(val RSI: Double?)

@Serializable
data class CciResponse(val CCI: Double?)

@Serializable
data class AdxResponse(val ADX: Double?)

@Serializable
data class StochRsiResponse(
    @SerialName("%K")
    val k: Double?,
    @SerialName("%D")
    val d: Double?
)

@Serializable
data class StochResponse(
    @SerialName("%K")
    val k: Double?,
    @SerialName("%D")
    val d: Double?
)

@Serializable
data class AroonResponse(
    @SerialName("Aroon Up")
    val aroonUp: Double?,
    @SerialName("Aroon Down")
    val aroonDown: Double?
)

@Serializable
data class BBandsResponse(
    @SerialName("Upper Band")
    val upperBand: Double?,
    @SerialName("Middle Band")
    val middleBand: Double?,
    @SerialName("Lower Band")
    val lowerBand: Double?
)

@Serializable
data class MacdResponse(
    @SerialName("MACD")
    val macd: Double?,
    @SerialName("Signal")
    val signal: Double?,
)

@Serializable
data class SuperTrendResponse(
    @SerialName("Super Trend")
    val superTrend: Double?,
    @SerialName("Trend")
    val trend: String?
)

@Serializable
data class IchimokuCloudResponse(
    @SerialName("Conversion Line")
    val conversionLine: Double?,
    @SerialName("Base Line")
    val baseLine: Double?,
    @SerialName("Lagging Span")
    val laggingSpan: Double?,
    @SerialName("Leading Span A")
    val leadingSpanA: Double?,
    @SerialName("Leading Span B")
    val leadingSpanB: Double?
)