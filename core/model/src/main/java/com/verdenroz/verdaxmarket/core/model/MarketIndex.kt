package com.verdenroz.verdaxmarket.core.model


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

private val unavailableIndices = setOf(
    "MOEX Russia Index",
    "RTSI",
    "DJ New Zealand",
    "China A50",
    "DJ Shanghai",
    "Karachi 100",
    "VN 30"
)

fun MarketIndex.toSymbol(): String =
    when (name) {
        "S&P 500" -> "^GSPC"
        "Dow Jones" -> "^DJI"
        "Nasdaq" -> "^IXIC"
        "Small Cap 2000" -> "^RUT"
        "S&P 500 VIX" -> "^VIX"
        "S&P/TSX" -> "^GSPTSE"
        "Bovespa" -> "^BVSP"
        "S&P/BMV IPC" -> "^MXX"
        "MSCI World" -> "^990100-USD-STRD"
        "DAX" -> "^GDAXI"
        "FTSE 100" -> "^FTSE"
        "CAC 40" -> "^FCHI"
        "Euro Stoxx 50" -> "^STOXX50E"
        "AEX" -> "^AEX"
        "IBEX 35" -> "^IBEX"
        "FTSE MIB" -> "^FTSEMIB"
        "SMI" -> "^SSMI"
        "PSI" -> "PSI20.LS"
        "BEL 20" -> "^BFX"
        "ATX" -> "^ATX"
        "OMXS30" -> "^OMX"
        "OMXC25" -> "^OMXC25"
        // "MOEX Russia Index" -> "IMOEX.ME"  (Cannot be provided by FinanceQuery)
        // "RTSI" -> "RTSI.ME" (Cannot be provided by FinanceQuery)
        "WIG20" -> "WIG20.WA"
        "Budapest SE" -> "^BUX.BD"
        "BIST 100" -> "XU100.IS"
        "TA 35" -> "TA35.TA"
        "Tadawul All Share" -> "^TASI.SR"
        "Nikkei 225" -> "^N225"
        "S&P/ASX 200" -> "^AXJO"
        // "DJ New Zealand" -> "^NZ50" (Cannot be provided by FinanceQuery)
        "Shanghai" -> "000001.SS"
        "SZSE Component" -> "399001.SZ"
        // "China A50" -> "XIN9.FGI" (Cannot be provided by FinanceQuery)
        // "DJ Shanghai" -> "^DJSH" (Cannot be provided by FinanceQuery)
        "Hang Seng" -> "^HSI"
        "Taiwan Weighted" -> "^TWII"
        "SET" -> "^SET.BK"
        "KOSPI" -> "^KS11"
        "IDX Composite" -> "^JKSE"
        "Nifty 50" -> "^NSEI"
        "BSE Sensex" -> "^BSESN"
        "PSEi Composite" -> "PSEI.PS"
        // "Karachi 100" -> "^KSE" (Cannot be provided by FinanceQuery)
        // "VN 30" -> "^VNINDEX" (Cannot be provided by FinanceQuery)
        else -> throw IllegalArgumentException("No symbol mapping for index: $name")
    }

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
        } && index.name !in unavailableIndices
    }
