package com.verdenroz.verdaxmarket.core.data.model

import com.verdenroz.verdaxmarket.core.database.model.QuoteEntity
import com.verdenroz.verdaxmarket.core.model.FullQuoteData
import com.verdenroz.verdaxmarket.core.model.SimpleQuoteData
import com.verdenroz.verdaxmarket.core.network.model.FullQuoteResponse
import com.verdenroz.verdaxmarket.core.network.model.SimpleQuoteResponse

fun SimpleQuoteResponse.asExternalModel() = SimpleQuoteData(
    symbol = symbol,
    name = name,
    price = price,
    change = change,
    percentChange = percentChange,
)

fun SimpleQuoteResponse.asEntity() = QuoteEntity(
    symbol = symbol,
    name = name,
    price = price,
    change = change,
    percentChange = percentChange,
)

fun QuoteEntity.asExternalModel() = SimpleQuoteData(
    symbol = symbol,
    name = name,
    price = price,
    change = change,
    percentChange = percentChange,
)


fun List<SimpleQuoteResponse>.asExternalModel() = map { it.asExternalModel() }

fun List<SimpleQuoteResponse>.asEntity() = map { it.asEntity() }

fun List<QuoteEntity>.toExternal() = map { it.asExternalModel() }

fun FullQuoteResponse.asExternalModel() = FullQuoteData(
    symbol = symbol,
    name = name,
    price = price,
    afterHoursPrice = afterHoursPrice,
    change = change,
    percentChange = percentChange,
    open = open,
    high = high,
    low = low,
    yearHigh = yearHigh,
    yearLow = yearLow,
    volume = volume?.replace(",", "")?.toLong(),
    avgVolume = avgVolume?.replace(",", "")?.toLong(),
    marketCap = marketCap,
    beta = beta,
    eps = eps,
    pe = pe,
    dividend = dividend,
    yield = yield,
    netAssets = netAssets,
    nav = nav,
    expenseRatio = expenseRatio,
    category = category,
    lastCapitalGain = lastCapitalGain,
    morningstarRating = morningstarRating,
    morningstarRisk = morningstarRiskRating,
    holdingsTurnover = holdingsTurnover,
    lastDividend = lastDividend,
    inceptionDate = inceptionDate,
    exDividend = exDividend,
    earningsDate = earningsDate,
    sector = sector,
    industry = industry,
    about = about,
    ytdReturn = ytdReturn,
    yearReturn = yearReturn,
    threeYearReturn = threeYearReturn,
    fiveYearReturn = fiveYearReturn,
    logo = logo,
)