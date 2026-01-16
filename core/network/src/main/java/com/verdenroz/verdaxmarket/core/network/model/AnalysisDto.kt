package com.verdenroz.verdaxmarket.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AnalysisDto(
    @SerialName("SMA(10)")
    val sma10: SmaDto? = null,
    @SerialName("SMA(20)")
    val sma20: SmaDto? = null,
    @SerialName("SMA(50)")
    val sma50: SmaDto? = null,
    @SerialName("SMA(100)")
    val sma100: SmaDto? = null,
    @SerialName("SMA(200)")
    val sma200: SmaDto? = null,
    @SerialName("EMA(10)")
    val ema10: EmaDto? = null,
    @SerialName("EMA(20)")
    val ema20: EmaDto? = null,
    @SerialName("EMA(50)")
    val ema50: EmaDto? = null,
    @SerialName("EMA(100)")
    val ema100: EmaDto? = null,
    @SerialName("EMA(200)")
    val ema200: EmaDto? = null,
    @SerialName("WMA(10)")
    val wma10: WmaDto? = null,
    @SerialName("WMA(20)")
    val wma20: WmaDto? = null,
    @SerialName("WMA(50)")
    val wma50: WmaDto? = null,
    @SerialName("WMA(100)")
    val wma100: WmaDto? = null,
    @SerialName("WMA(200)")
    val wma200: WmaDto? = null,
    @SerialName("VWMA(20)")
    val vwma20: VwmaDto? = null,
    @SerialName("RSI(14)")
    val rsi14: RsiDto? = null,
    @SerialName("SRSI(3,3,14,14)")
    val srsi14: StochRsiDto? = null,
    @SerialName("CCI(20)")
    val cci20: CciDto? = null,
    @SerialName("ADX(14)")
    val adx14: AdxDto? = null,
    @SerialName("MACD(12,26)")
    val macd: MacdDto? = null,
    @SerialName("STOCH %K(14,3,3)")
    val stoch: StochDto? = null,
    @SerialName("Aroon(25)")
    val aroon: AroonDto? = null,
    @SerialName("BBANDS(20,2)")
    val bBands: BBandsDto? = null,
    @SerialName("Super Trend")
    val superTrend: SuperTrendDto? = null,
    @SerialName("Ichimoku Cloud")
    val ichimokuCloud: IchimokuCloudDto? = null
)

@Serializable
data class SmaDto(val SMA: Double?)

@Serializable
data class EmaDto(val EMA: Double?)

@Serializable
data class WmaDto(val WMA: Double?)

@Serializable
data class VwmaDto(val VWMA: Double?)

@Serializable
data class RsiDto(val RSI: Double?)

@Serializable
data class CciDto(val CCI: Double?)

@Serializable
data class AdxDto(val ADX: Double?)

@Serializable
data class StochRsiDto(
    @SerialName("%K")
    val k: Double?,
    @SerialName("%D")
    val d: Double?
)

@Serializable
data class StochDto(
    @SerialName("%K")
    val k: Double?,
    @SerialName("%D")
    val d: Double?
)

@Serializable
data class AroonDto(
    @SerialName("Aroon Up")
    val aroonUp: Double?,
    @SerialName("Aroon Down")
    val aroonDown: Double?
)

@Serializable
data class BBandsDto(
    @SerialName("Upper Band")
    val upperBand: Double?,
    @SerialName("Middle Band")
    val middleBand: Double?,
    @SerialName("Lower Band")
    val lowerBand: Double?
)

@Serializable
data class MacdDto(
    @SerialName("MACD")
    val macd: Double?,
    @SerialName("Signal")
    val signal: Double?,
)

@Serializable
data class SuperTrendDto(
    @SerialName("Super Trend")
    val superTrend: Double?,
    @SerialName("Trend")
    val trend: String?
)

@Serializable
data class IchimokuCloudDto(
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
