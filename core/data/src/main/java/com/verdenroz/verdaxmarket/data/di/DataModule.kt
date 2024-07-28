package com.verdenroz.verdaxmarket.data.di

import com.verdenroz.verdaxmarket.data.utils.ConnectivityManagerNetworkMonitor
import com.verdenroz.verdaxmarket.data.utils.NetworkMonitor
import com.verdenroz.verdaxmarket.data.repository.FinanceQueryRepository
import com.verdenroz.verdaxmarket.data.repository.ImplFinanceQueryRepository
import com.verdenroz.verdaxmarket.data.utils.MarketMonitor
import com.verdenroz.verdaxmarket.data.utils.MarketStatusMonitor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    internal abstract fun bindsFinanceQueryRepository(
        financeQueryRepository: ImplFinanceQueryRepository,
    ): FinanceQueryRepository

    @Binds
    internal abstract fun bindsNetworkMonitor(
        networkMonitor: ConnectivityManagerNetworkMonitor,
    ): NetworkMonitor

    @Binds
    internal abstract fun bindsMarketMonitor(
        marketMonitor: MarketStatusMonitor,
    ): MarketMonitor

}
