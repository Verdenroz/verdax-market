package com.verdenroz.verdaxmarket.core.model

/**
 * Filter for region of stock exchanges
 */
enum class RegionFilter(val exchanges: Set<String>) {
    US(setOf("NASDAQ", "NYSE", "AMEX", "ETF", "CBOE")),
    NA(
        setOf(
            "NASDAQ",
            "NYSE",
            "AMEX",
            "ETF",
            "PNK",
            "OTC",
            "PNK",
            "OTC",
            "CBOE",
            "NEO",
            "TSX",
            "TSXV",
            "CNQ",
            "MEX"
        )
    ),
    SA(setOf("SAO", "BUE", "SGO")),
    EU(
        setOf(
            "LSE",
            "AQS",
            "XETRA",
            "STU",
            "BER",
            "DUS",
            "MUN",
            "HAM",
            "BME",
            "PRA",
            "EURONEXT",
            "PAR",
            "BRU",
            "STO",
            "ICE",
            "CPH",
            "HEL",
            "RIS",
            "MIL",
            "WSE",
            "SIX",
            "OSL",
            "VIE",
            "SES",
            "BUD",
            "AMS",
            "ATH",
            "IST",
            "DXE",
            "IOB",
        )
    ),
    AS(
        setOf(
            "BSE",
            "NSE",
            "JPX",
            "SHZ",
            "SHH",
            "HKSE",
            "KSC",
            "KLS",
            "KOE",
            "TAI",
            "TWO",
            "SET",
            "JKT",
            "CAI"
        )
    ),
    AF(setOf("JNB", "EGY", "CAI")),
    AU(setOf("ASX", "NZE")),
    ME(setOf("TLV", "SAU", "DOH", "DFM", "KUW")),
    GLOBAL((NA.exchanges + SA.exchanges + EU.exchanges + AS.exchanges + AF.exchanges + AU.exchanges + ME.exchanges).toSet())
}

fun RegionFilter.toSymbols(): Set<String> {
    return when (this) {
        RegionFilter.US -> setOf("^GSPC", "^DJI", "^IXIC", "^RUT", "^VIX")
        RegionFilter.NA -> setOf("^GSPC", "^DJI", "^IXIC", "^RUT", "^VIX", "^GSPTSE")
        RegionFilter.SA -> setOf("^BVSP", "^MXX")
        RegionFilter.EU -> setOf(
            "^GDAXI", "^FTSE", "^FCHI", "^STOXX50E", "^AEX", "^IBEX",
            "^FTSEMIB", "^SSMI", "PSI20.LS", "^BFX", "^ATX", "^OMX", "^OMXC25",
            "WIG20.WA", "^BUX.BD", "XU100.IS"
        )
        RegionFilter.AS -> setOf(
            "^N225", "000001.SS", "399001.SZ", "XIN9.FGI", "^DJSH", "^HSI",
            "^TWII", "^SET.BK", "^KS11", "^JKSE", "^NSEI", "^BSESN", "PSEI.PS"
        )
        RegionFilter.AF -> setOf("^990100-USD-STRD")
        RegionFilter.AU -> setOf("^AXJO")
        RegionFilter.ME -> setOf("TA35.TA", "^TASI.SR")
        RegionFilter.GLOBAL -> setOf(
            "^GSPC", "^DJI", "^IXIC", "^RUT", "^VIX", "^GSPTSE", "^BVSP", "^MXX",
            "^GDAXI", "^FTSE", "^FCHI", "^STOXX50E", "^AEX", "^IBEX", "^FTSEMIB",
            "^SSMI", "PSI20.LS", "^BFX", "^ATX", "^OMX", "^OMXC25", "WIG20.WA",
            "^BUX.BD", "XU100.IS", "^N225", "000001.SS", "399001.SZ", "XIN9.FGI",
            "^DJSH", "^HSI", "^TWII", "^SET.BK", "^KS11", "^JKSE", "^NSEI",
            "^BSESN", "PSEI.PS", "^990100-USD-STRD", "^AXJO", "TA35.TA", "^TASI.SR"
        )
    }
}

/**
 * Filter for type of stock
 */
enum class TypeFilter(val type: String) {
    STOCK("stock"),
    ETF("etf"),
    TRUST("trust"),
}
