package com.verdenroz.verdaxmarket.core.model.enums

internal enum class Index(val symbol: String) {
    // United States
    GSPC("snp"),                 // S&P 500
    DJI("djia"),                 // Dow Jones Industrial Average
    IXIC("nasdaq"),              // NASDAQ Composite
    NYA("nyse-composite"),       // NYSE Composite
    XAX("nyse-amex"),            // NYSE American Composite
    RUT("rut"),                  // Russell 2000
    VIX("vix"),                  // CBOE Volatility Index

    // North America (excluding US)
    GSPTSE("tsx-composite"),     // Toronto Stock Exchange

    // South America
    BVSP("ibovespa"),            // Brazil Bovespa
    MXX("ipc-mexico"),           // Mexican IPC
    IPSA("ipsa"),                // Chile IPSA
    MERV("merval"),              // Argentina Merval
    IVBX("ivbx"),                // Brazil IVBX
    IBRX_50("ibrx-50"),          // Brazil IBrX-50

    // Europe
    FTSE("ftse-100"),            // FTSE 100
    GDAXI("dax"),                // German DAX
    FCHI("cac-40"),              // French CAC 40
    STOXX50E("euro-stoxx-50"),   // Euro Stoxx 50
    N100("euronext-100"),        // Euronext 100
    BFX("bel-20"),               // Belgian BEL 20
    MOEX_ME("moex"),             // Moscow Exchange
    AEX("aex"),                  // Amsterdam Exchange
    IBEX("ibex-35"),             // Spanish IBEX 35
    FTSEMIB("ftse-mib"),         // Italian FTSE MIB
    SSMI("smi"),                 // Swiss Market Index
    PSI("psi"),                  // Portuguese PSI
    ATX("atx"),                  // Austrian ATX
    OMXS30("omxs30"),            // Stockholm OMX 30
    OMXC25("omxc25"),            // Copenhagen OMX 25
    WIG20("wig20"),              // Warsaw WIG 20
    BUX("budapest-se"),          // Budapest Stock Exchange
    IMOEX("moex-russia"),        // Moscow Exchange Russia
    RTSI("rtsi"),                // Russian Trading System

    // Asia
    HSI("hang-seng"),            // Hong Kong Hang Seng
    STI("sti"),                  // Singapore Straits Times
    BSESN("sensex"),             // BSE Sensex (India)
    JKSE("idx-composite"),       // Jakarta Composite
    KLSE("ftse-bursa"),          // FTSE Bursa Malaysia
    KS11("kospi"),               // Korea KOSPI
    TWII("twse"),                // Taiwan TAIEX
    N225("nikkei-225"),          // Nikkei 225
    SHANGHAI("shanghai"),        // Shanghai Composite
    SZSE("szse-component"),      // Shenzhen Component
    SET("set"),                  // Thailand SET
    NSEI("nifty-50"),            // NSE Nifty 50 (India)
    CNX200("nifty-200"),         // NSE Nifty 200
    PSEI("psei-composite"),      // Philippines PSEi Composite
    CHINA_A50("china-a50"),      // FTSE China A50
    DJSH("dj-shanghai"),         // Dow Jones Shanghai
    INDIAVIX("india-vix"),       // India VIX

    // Africa
    CASE30("egx-30"),            // Egypt EGX 30
    JN0U_JO("jse-40"),           // FTSE JSE Top 40- USD Net TRI
    FTSEJSE("ftse-jse"),         // FTSE/JSE SA Financials Index
    AFR40("afr-40"),             // All Africa 40 Rand Index
    RAF40("raf-40"),             // RAFI 40 Index
    SA40("sa-40"),               // South Africa Top 40
    ALT15("alt-15"),             // Alternative 15

    // Middle East
    TA125_TA("ta-125"),          // Tel Aviv 125
    TA35("ta-35"),               // Tel Aviv 35
    TASI("tadawul-all-share"),   // Tadawul All Share
    TAMAYUZ("tamayuz"),          // Egyptian Tamayuz
    BIST100("bist-100"),         // Borsa Istanbul 100

    // Oceania
    AXJO("asx-200"),             // ASX 200 (Australia)
    AORD("all-ordinaries"),      // All Ordinaries (Australia)
    NZ50("nzx-50"),              // NZX 50 (New Zealand)

    // Global/Currency
    DX_Y_NYB("usd"),             // US Dollar Index
    USD_STRD("msci-europe"),     // MSCI Europe USD
    XDB("gbp"),                  // British Pound
    XDE("euro"),                 // Euro
    XDN("yen"),                  // Japanese Yen
    XDA("australian"),           // Australian Dollar
    MSCI_WORLD("msci-world"),    // MSCI World Index
    BUK100P("cboe-uk-100");      // CBOE UK 100
}

internal fun Index.toYahooSymbol(): String = when (this) {
    Index.MOEX_ME -> "MOEX.ME"
    Index.DX_Y_NYB -> "DX-Y.NYB"
    Index.USD_STRD -> "^125904-USD-STRD"
    Index.MSCI_WORLD -> "^990100-USD-STRD"
    Index.SHANGHAI -> "000001.SS"
    Index.SZSE -> "399001.SZ"
    Index.PSI -> "PSI20.LS"
    Index.BUX -> "^BUX.BD"
    Index.BIST100 -> "XU100.IS"
    Index.TA35 -> "TA35.TA"
    Index.TASI -> "^TASI.SR"
    Index.SET -> "^SET.BK"
    Index.PSEI -> "PSEI.PS"
    Index.IMOEX -> "IMOEX.ME"
    Index.RTSI -> "RTSI.ME"
    Index.CHINA_A50 -> "XIN9.FGI"
    Index.WIG20 -> "WIG20.WA"
    Index.FTSEMIB -> "FTSEMIB.MI"
    Index.FTSEJSE -> "^J580.JO"
    Index.JN0U_JO -> "^JN0U.JO"
    Index.AFR40 -> "^JA0R.JO"
    Index.SA40 -> "^J200.JO"
    Index.RAF40 -> "^J260.JO"
    Index.ALT15 -> "^J233.JO"
    Index.TAMAYUZ -> "^TAMAYUZ.CA"
    Index.IVBX -> "^IVBX"
    Index.IBRX_50 -> "^IBX50"
    Index.TA125_TA -> "TA125.TA"
    Index.NZ50 -> "^NZ50"
    else -> "^${this.name}"
}

enum class RegionFilter(internal val indices: Set<Index>) {
    US(setOf(
        Index.GSPC, Index.DJI, Index.IXIC, Index.NYA, Index.XAX, Index.RUT, Index.VIX
    )),

    NA(setOf(
        Index.GSPC, Index.DJI, Index.GSPTSE, Index.IXIC, Index.NYA, Index.XAX, Index.RUT, Index.VIX
    )),

    SA(setOf(
        Index.BVSP, Index.MXX, Index.IPSA, Index.MERV, Index.IVBX, Index.IBRX_50
    )),

    EU(setOf(
        Index.FTSE, Index.GDAXI, Index.FCHI, Index.STOXX50E, Index.N100, Index.BFX,
        Index.MOEX_ME, Index.AEX, Index.IBEX, Index.FTSEMIB, Index.SSMI, Index.PSI,
        Index.ATX, Index.OMXS30, Index.OMXC25, Index.WIG20, Index.BUX, Index.IMOEX,
        Index.RTSI
    )),

    AS(setOf(
        Index.HSI, Index.STI, Index.BSESN, Index.JKSE, Index.KLSE, Index.KS11,
        Index.TWII, Index.N225, Index.SHANGHAI, Index.SZSE, Index.SET,
        Index.NSEI, Index.CNX200, Index.PSEI, Index.CHINA_A50, Index.DJSH, Index.INDIAVIX
    )),

    AF(setOf(
        Index.CASE30, Index.JN0U_JO, Index.FTSEJSE, Index.AFR40, Index.SA40,
        Index.RAF40, Index.ALT15
    )),

    ME(setOf(
        Index.TA125_TA, Index.TA35, Index.TASI, Index.TAMAYUZ, Index.BIST100
    )),

    OCE(setOf(
        Index.AXJO, Index.AORD, Index.NZ50
    )),

    GLOBAL(setOf(
        Index.DX_Y_NYB, Index.USD_STRD, Index.XDB, Index.XDE, Index.XDN,
        Index.XDA, Index.MSCI_WORLD, Index.BUK100P
    ));

}

fun RegionFilter.toSymbols(): Set<String> = indices.map { it.toYahooSymbol() }.toSet()


fun RegionFilter.toExchangeShortNames(): Set<String> {
    /**
     *  Converts [RegionFilter] to a set of exchange short names for search filtering
     */
    return when (this) {
        RegionFilter.US -> setOf("NASDAQ", "NYSE", "AMEX", "ETF", "CBOE")
        RegionFilter.NA -> setOf(
            "NASDAQ", "NYSE", "AMEX", "ETF", "PNK", "OTC", "CBOE",
            "NEO", "TSX", "TSXV", "CNQ", "MEX"
        )
        RegionFilter.SA -> setOf("SAO", "BUE", "SGO")
        RegionFilter.EU -> setOf(
            "LSE", "AQS", "XETRA", "STU", "BER", "DUS", "MUN", "HAM",
            "BME", "PRA", "EURONEXT", "PAR", "BRU", "STO", "ICE",
            "CPH", "HEL", "RIS", "MIL", "WSE", "SIX", "OSL", "VIE",
            "SES", "BUD", "AMS", "ATH", "IST", "DXE", "IOB"
        )
        RegionFilter.AS -> setOf(
            "BSE", "NSE", "JPX", "SHZ", "SHH", "HKSE", "KSC", "KLS",
            "KOE", "TAI", "TWO", "SET", "JKT", "CAI"
        )
        RegionFilter.AF -> setOf("JNB", "EGY", "CAI")
        RegionFilter.OCE -> setOf("ASX", "NZE")  // Changed from AU to OCE
        RegionFilter.ME -> setOf("TLV", "SAU", "DOH", "DFM", "KUW")
        RegionFilter.GLOBAL -> {
            // Dynamically combine all exchange short names from other regions
            // except GLOBAL to avoid circular reference
            val regions = RegionFilter.entries.filter { it != RegionFilter.GLOBAL }
            regions.flatMap { it.toExchangeShortNames() }.toSet()
        }
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