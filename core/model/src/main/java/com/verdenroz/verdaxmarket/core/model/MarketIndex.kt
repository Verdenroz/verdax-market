package com.verdenroz.verdaxmarket.core.model

import com.verdenroz.verdaxmarket.core.model.enums.RegionFilter
import com.verdenroz.verdaxmarket.core.model.enums.toYahooSymbol

/**
 * Local data class for stock market indices
 * @param name Name of the index
 * @param value Current value of the index
 * @param change Change in value the index
 * @param percentChange Percent change in value of the index
 * @param fiveDaysReturn Return over the last 5 days
 * @param oneMonthReturn Return over the last month
 * @param threeMonthReturn Return over the last 3 months
 * @param sixMonthReturn Return over the last 6 months
 * @param ytdReturn Return over the year to date
 * @param yearReturn Return over the last year
 * @param threeYearReturn Return over the last 3 years
 * @param fiveYearReturn Return over the last 5 years
 * @param tenYearReturn Return over the last 10 years
 * @param maxReturn Maximum historical return
 */
data class MarketIndex(
    val name: String,
    val value: String,
    val change: String,
    val percentChange: String,
    val fiveDaysReturn: String? = null,
    val oneMonthReturn: String? = null,
    val threeMonthReturn: String? = null,
    val sixMonthReturn: String? = null,
    val ytdReturn: String? = null,
    val yearReturn: String? = null,
    val threeYearReturn: String? = null,
    val fiveYearReturn: String? = null,
    val tenYearReturn: String? = null,
    val maxReturn: String? = null,
)

/**
 * Mapping of Yahoo Finance symbols to human-readable market index names
 */
private val symbolToNameMap = mapOf(
    // United States
    "^GSPC" to "S&P 500",
    "^DJI" to "Dow Jones Industrial Average",
    "^IXIC" to "NASDAQ Composite",
    "^NYA" to "NYSE COMPOSITE (DJ)",
    "^XAX" to "NYSE AMEX COMPOSITE INDEX",
    "^RUT" to "Russell 2000",
    "^VIX" to "CBOE Volatility Index",

    // North America
    "^GSPTSE" to "S&P/TSX Composite index",

    // South America
    "^BVSP" to "IBOVESPA",
    "^MXX" to "IPC MEXICO",
    "^IPSA" to "S&P IPSA",
    "^MERV" to "MERVAL",
    "^IVBX" to "IVBX2",
    "^IBX50" to "IBRX 50",

    // Europe
    "^GDAXI" to "DAX Performance Index",
    "^FTSE" to "FTSE 100",
    "^FCHI" to "CAC 40",
    "^STOXX50E" to "EURO STOXX 50",
    "^N100" to "Euronext 100 Index",
    "^BFX" to "BEL 20",
    "MOEX.ME" to "Public Joint-Stock Company Moscow Exchange MICEX-RTS",
    "^AEX" to "AEX-Index",
    "^IBEX" to "IBEX 35",
    "FTSEMIB.MI" to "FTSE MIB Index",
    "^SSMI" to "SMI PR",
    "PSI20.LS" to "PSI",
    "^ATX" to "Austrian Traded Index in EUR",
    "^OMXS30" to "XCSE:OMX Stockholm 30 Index",
    "^OMXC25" to "OMX Copenhagen 25 Index",
    "WIG20.WA" to "WIG20",
    "^BUX.BD" to "Budapest Stock Index",
    "IMOEX.ME" to "MOEX Russia Index",
    "RTSI.ME" to "RTS Index",

    // Asia
    "^HSI" to "HANG SENG INDEX",
    "^STI" to "STI Index",
    "^BSESN" to "S&P BSE SENSEX",
    "^JKSE" to "IDX COMPOSITE",
    "^KLSE" to "FTSE Bursa Malaysia KLCI",
    "^KS11" to "KOSPI Composite Index",
    "^TWII" to "TWSE Capitalization Weighted Stock Index",
    "^N225" to "Nikkei 225",
    "000001.SS" to "SSE Composite Index",
    "399001.SZ" to "Shenzhen Index",
    "^SET.BK" to "Thailand SET Index",
    "^NSEI" to "NIFTY 50",
    "^CNX200" to "NIFTY 200",
    "PSEI.PS" to "PSEi INDEX",
    "XIN9.FGI" to "FTSE China A50 Index",
    "^DJSH" to "Dow Jones Shanghai Index",
    "^INDIAVIX" to "INDIA VIX",

    // Africa
    "^CASE30" to "EGX 30 Price Return Index",
    "^JN0U.JO" to "FTSE JSE Top 40- USD Net TRI",
    "^J580.JO" to "FTSE/JSE SA Financials Index",
    "^JA0R.JO" to "All Africa 40 Rand Index",
    "^J260.JO" to "RAFI 40 Index",
    "^J200.JO" to "South Africa Top 40",
    "^J233.JO" to "ALT X 15 Index",

    // Middle East
    "TA125.TA" to "TA-125",
    "TA35.TA" to "TA-35",
    "^TASI.SR" to "Tadawul All Shares Index",
    "^TAMAYUZ.CA" to "TAMAYUZ",
    "XU100.IS" to "BIST 100",

    // Oceania
    "^AXJO" to "S&P/ASX 200",
    "^AORD" to "ALL ORDINARIES",
    "^NZ50" to "S&P/NZX 50 Index",

    // Global/Currency
    "DX-Y.NYB" to "US Dollar Index",
    "^125904-USD-STRD" to "MSCI EUROPE",
    "^XDB" to "British Pound Currency Index",
    "^XDE" to "Euro Currency Index",
    "^XDN" to "Japanese Yen Currency Index",
    "^XDA" to "Australian Dollar Currency Index",
    "^990100-USD-STRD" to "MSCI WORLD",
    "^BUK100P" to "Cboe UK 100",
)

/**
 * Converts a symbol to a human-readable market index name
 */
fun String.toMarketIndexName(): String =
    symbolToNameMap[this]
        ?: throw IllegalArgumentException("No market index name mapping for symbol: $this")

/**
 * Filters a list of market indices by [RegionFilter]
 */
fun List<MarketIndex>.filterByRegion(region: RegionFilter): List<MarketIndex> {
    // Get all names for the selected region's indices
    val indexNames = region.indices.map { index ->
        index.toYahooSymbol().toMarketIndexName()
    }.toSet()

    // If GLOBAL is selected, return all indices
    return if (region == RegionFilter.GLOBAL) {
        this
    } else {
        // Otherwise filter by the names associated with the region
        filter { marketIndex -> marketIndex.name in indexNames }
    }
}
