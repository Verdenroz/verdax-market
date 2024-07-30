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

/**
 * Filter for type of stock
 */
enum class TypeFilter(val type: String) {
    STOCK("stock"),
    ETF("etf"),
    TRUST("trust"),
}
