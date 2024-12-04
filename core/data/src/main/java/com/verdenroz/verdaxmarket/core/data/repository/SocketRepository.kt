package com.verdenroz.verdaxmarket.core.data.repository

import com.verdenroz.verdaxmarket.core.common.error.DataError
import com.verdenroz.verdaxmarket.core.common.result.Result
import com.verdenroz.verdaxmarket.core.model.MarketInfo
import com.verdenroz.verdaxmarket.core.model.Profile
import com.verdenroz.verdaxmarket.core.model.SimpleQuoteData
import kotlinx.coroutines.flow.Flow

interface SocketRepository {

    val market: Flow<Result<MarketInfo, DataError.Network>>

    fun getProfile(symbol: String): Flow<Result<Profile, DataError.Network>>

    fun getQuotes(symbols: List<String>): Flow<Result<List<SimpleQuoteData>, DataError.Network>>
}