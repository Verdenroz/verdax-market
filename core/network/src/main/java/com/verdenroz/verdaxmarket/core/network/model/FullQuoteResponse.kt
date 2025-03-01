package com.verdenroz.verdaxmarket.core.network.model

import kotlinx.serialization.Serializable

/**
 * This class is used to parse the response from the FinanceQuery API when requesting a full quote for a stock.
 * @param symbol the stock symbol
 * @param name the stock name
 * @param price the current price
 * @param preMarketPrice the price before market open
 * @param afterHoursPrice the price after market close
 * @param change the change in price
 * @param percentChange the percentage change in price
 * @param open the opening price of the day
 * @param high the highest price of the day
 * @param low the lowest price of the day
 * @param yearHigh the highest price of the year
 * @param yearLow the lowest price of the year
 * @param volume the number of shares traded in a day
 * @param avgVolume the average volume traded in a day
 * @param marketCap the market capitalization
 * @param beta the beta value
 * @param eps the earnings per share
 * @param pe the price to earnings ratio
 * @param dividend the dividend amount
 * @param yield the dividend yield
 * @param netAssets the net assets
 * @param nav the net asset value
 * @param expenseRatio the expense ratio
 * @param category the category the fund belongs to
 * @param lastCapitalGain the last capital gain of the fund
 * @param morningstarRating the morningstar rating of the fund
 * @param morningstarRiskRating the morningstar risk of the fund
 * @param holdingsTurnover the holdings turnover of the fund
 * @param lastDividend the last dividend date
 * @param inceptionDate the inception date of the fund
 * @param exDividend the last date before which the stock must be bought to receive the dividend
 * @param earningsDate the earnings announcement date
 * @param sector the sector the stock belongs to
 * @param industry the industry the stock belongs to
 * @param about a brief description of the company
 * @param ytdReturn the year to date return
 * @param yearReturn the one year return
 * @param threeYearReturn the three year return
 * @param fiveYearReturn the five year return
 * @param logo the URL of the company logo
 * @param employees the number of employees
 */
@Serializable
data class FullQuoteResponse(
    val symbol: String,
    val name: String,
    val price: String,
    val preMarketPrice: String? = null,
    val afterHoursPrice: String? = null,
    val change: String,
    val percentChange: String,
    val open: String? = null,
    val high: String? = null,
    val low: String? = null,
    val yearHigh: String? = null,
    val yearLow: String? = null,
    val volume: Long? = null,
    val avgVolume: Long? = null,
    val marketCap: String? = null,
    val beta: String? = null,
    val pe: String? = null,
    val eps: String? = null,
    val dividend: String? = null,
    val yield: String? = null,
    val netAssets: String? = null,
    val nav: String? = null,
    val expenseRatio: String? = null,
    val category: String? = null,
    val lastCapitalGain: String? = null,
    val morningstarRating: String? = null,
    val morningstarRiskRating: String? = null,
    val holdingsTurnover: String? = null,
    val lastDividend: String? = null,
    val inceptionDate: String? = null,
    val exDividend: String? = null,
    val earningsDate: String? = null,
    val sector: String? = null,
    val industry: String? = null,
    val about: String? = null,
    val ytdReturn: String? = null,
    val yearReturn: String? = null,
    val threeYearReturn: String? = null,
    val fiveYearReturn: String? = null,
    val logo: String? = null,
    val employees: String? = null,
)
