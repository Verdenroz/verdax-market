package com.verdenroz.verdaxmarket.core.model

import com.verdenroz.verdaxmarket.core.model.enums.RegionFilter


/**
 * Local data class for stock market indices
 * @param name Name of the index
 * @param value Current value of the index
 * @param change Change in value the index
 * @param percentChange Percent change in value of the index
 */
data class MarketIndex(
    val name: String,
    val value: String,
    val change: String,
    val percentChange: String
)

//private val unavailableIndices = setOf(
//    "MOEX Russia Index",
//    "RTSI",
//    "DJ New Zealand",
//    "China A50",
//    "DJ Shanghai",
//    "Karachi 100",
//    "VN 30"
//)

/**
 * Filters a list of market indices by [com.verdenroz.verdaxmarket.core.model.enums.RegionFilter]
 */
fun List<MarketIndex>.filterByRegion(region: RegionFilter): List<MarketIndex> =
    filter { index ->
        when (region) {
            RegionFilter.US -> index.name in setOf("Dow Jones", "S&P 500", "Nasdaq", "Small Cap 2000", "S&P 500 VIX")
            RegionFilter.NA -> index.name in setOf("Dow Jones", "S&P 500", "Nasdaq", "Small Cap 2000", "S&P 500 VIX", "S&P/TSX")
            RegionFilter.SA -> index.name in setOf("Bovespa", "S&P/BMV IPC")
            RegionFilter.EU -> index.name in setOf(
                "DAX", "FTSE 100", "CAC 40", "Euro Stoxx 50", "AEX", "IBEX 35",
                "FTSE MIB", "SMI", "PSI", "BEL 20", "ATX", "OMXS30", "OMXC25",
                "MOEX Russia Index", "RTSI", "WIG20", "Budapest SE", "BIST 100"
            )
            RegionFilter.AS -> index.name in setOf(
                "Nikkei 225", "Shanghai", "SZSE Component", "China A50",
                "DJ Shanghai", "Hang Seng", "Taiwan Weighted", "SET", "KOSPI",
                "IDX Composite", "Nifty 50", "BSE Sensex", "PSEi Composite",
                "Karachi 100", "VN 30"
            )
            RegionFilter.AF -> index.name in setOf("MSCI World")
            RegionFilter.AU -> index.name in setOf("S&P/ASX 200", "DJ New Zealand")
            RegionFilter.ME -> index.name in setOf("TA 35", "Tadawul All Share")
            RegionFilter.GLOBAL -> true
        }
    }

/**
 * Converts a symbol to a human-readable market index name
 */
fun String.toMarketIndexName(): String =
    when (this) {
        "^GSPC" -> "S&P 500"
        "^DJI" -> "Dow Jones"
        "^IXIC" -> "Nasdaq"
        "^RUT" -> "Small Cap 2000"
        "^VIX" -> "S&P 500 VIX"
        "^GSPTSE" -> "S&P/TSX"
        "^BVSP" -> "Bovespa"
        "^MXX" -> "S&P/BMV IPC"
        "^990100-USD-STRD" -> "MSCI World"
        "^GDAXI" -> "DAX"
        "^FTSE" -> "FTSE 100"
        "^FCHI" -> "CAC 40"
        "^STOXX50E" -> "Euro Stoxx 50"
        "^AEX" -> "AEX"
        "^IBEX" -> "IBEX 35"
        "^FTSEMIB" -> "FTSE MIB"
        "^SSMI" -> "SMI"
        "PSI20.LS" -> "PSI"
        "^BFX" -> "BEL 20"
        "^ATX" -> "ATX"
        "^OMX" -> "OMXS30"
        "^OMXC25" -> "OMXC25"
        "WIG20.WA" -> "WIG20"
        "^BUX.BD" -> "Budapest SE"
        "XU100.IS" -> "BIST 100"
        "TA35.TA" -> "TA 35"
        "^TASI.SR" -> "Tadawul All Share"
        "^N225" -> "Nikkei 225"
        "^AXJO" -> "S&P/ASX 200"
        "000001.SS" -> "Shanghai"
        "399001.SZ" -> "SZSE Component"
        "^HSI" -> "Hang Seng"
        "^TWII" -> "Taiwan Weighted"
        "^SET.BK" -> "SET"
        "^KS11" -> "KOSPI"
        "^JKSE" -> "IDX Composite"
        "^NSEI" -> "Nifty 50"
        "^BSESN" -> "BSE Sensex"
        "PSEI.PS" -> "PSEi Composite"
        "IMOEX.ME" -> "MOEX Russia Index"
        "RTSI.ME" -> "RTSI"
        "^NZ50" -> "DJ New Zealand"
        "XIN9.FGI" -> "China A50"
        "^DJSH" -> "DJ Shanghai"
        "^KSE" -> "Karachi 100"
        "^VNINDEX" -> "VN 30"
        else -> throw IllegalArgumentException("No market index name mapping for symbol: $this")
    }
