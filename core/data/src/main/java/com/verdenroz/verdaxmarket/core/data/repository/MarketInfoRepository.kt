package com.verdenroz.verdaxmarket.core.data.repository

import com.verdenroz.verdaxmarket.core.common.error.DataError
import com.verdenroz.verdaxmarket.core.common.result.Result
import com.verdenroz.verdaxmarket.core.model.MarketIndex
import com.verdenroz.verdaxmarket.core.model.MarketMover
import com.verdenroz.verdaxmarket.core.model.MarketSector
import com.verdenroz.verdaxmarket.core.model.News
import kotlinx.coroutines.flow.Flow

interface MarketInfoRepository {

    /**
     * The current market status of either open or closed
     */
    val marketStatus: Flow<Boolean>

    /**
     *  Market indices as list of [MarketIndex]
     */
    val indices: Flow<Result<List<MarketIndex>, DataError.Network>>

    /**
     * Active stocks as list of [MarketMover]
     */
    val actives: Flow<Result<List<MarketMover>, DataError.Network>>

    /**
     * Losers as list of [MarketMover]
     */
    val losers: Flow<Result<List<MarketMover>, DataError.Network>>

    /**
     * Gainers as list of [MarketMover]
     */
    val gainers: Flow<Result<List<MarketMover>, DataError.Network>>

    /**
     * Latest news headlines as list of [News]
     */
    val headlines: Flow<Result<List<News>, DataError.Network>>

    /**
     * Sectors as list of [MarketSector]
     */
    val sectors: Flow<Result<List<MarketSector>, DataError.Network>>

}