package com.verdenroz.verdaxmarket.core.data.di

import com.verdenroz.verdaxmarket.core.data.repository.ImplMarketInfoRepository
import com.verdenroz.verdaxmarket.core.data.utils.ConnectivityManagerNetworkMonitor
import com.verdenroz.verdaxmarket.core.data.utils.NetworkMonitor
import com.verdenroz.verdaxmarket.core.data.repository.QuoteRepository
import com.verdenroz.verdaxmarket.core.data.repository.ImplQuoteRepository
import com.verdenroz.verdaxmarket.core.data.repository.MarketInfoRepository
import com.verdenroz.verdaxmarket.core.data.utils.MarketMonitor
import com.verdenroz.verdaxmarket.core.data.utils.MarketStatusMonitor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    internal abstract fun bindsQuoteRepository(
        financeQueryRepository: ImplQuoteRepository,
    ): QuoteRepository

    @Binds
    internal abstract fun bindsMarketInfoRepository(
        marketInfoRepository: ImplMarketInfoRepository,
    ): MarketInfoRepository

    @Binds
    internal abstract fun bindsNetworkMonitor(
        networkMonitor: ConnectivityManagerNetworkMonitor,
    ): NetworkMonitor

    @Binds
    internal abstract fun bindsMarketMonitor(
        marketMonitor: MarketStatusMonitor,
    ): MarketMonitor

}
