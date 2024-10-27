package com.verdenroz.verdaxmarket.core.data.di

import com.verdenroz.verdaxmarket.core.data.repository.ImplMarketInfoRepository
import com.verdenroz.verdaxmarket.core.data.repository.ImplQuoteRepository
import com.verdenroz.verdaxmarket.core.data.repository.ImplRecentSearchRepository
import com.verdenroz.verdaxmarket.core.data.repository.ImplSignalRepository
import com.verdenroz.verdaxmarket.core.data.repository.ImplSocketRepository
import com.verdenroz.verdaxmarket.core.data.repository.ImplUserDataRepository
import com.verdenroz.verdaxmarket.core.data.repository.ImplWatchlistRepository
import com.verdenroz.verdaxmarket.core.data.repository.MarketInfoRepository
import com.verdenroz.verdaxmarket.core.data.repository.QuoteRepository
import com.verdenroz.verdaxmarket.core.data.repository.RecentSearchRepository
import com.verdenroz.verdaxmarket.core.data.repository.SignalRepository
import com.verdenroz.verdaxmarket.core.data.repository.SocketRepository
import com.verdenroz.verdaxmarket.core.data.repository.UserDataRepository
import com.verdenroz.verdaxmarket.core.data.repository.WatchlistRepository
import com.verdenroz.verdaxmarket.core.data.utils.ConnectivityManagerNetworkMonitor
import com.verdenroz.verdaxmarket.core.data.utils.MarketMonitor
import com.verdenroz.verdaxmarket.core.data.utils.MarketStatusMonitor
import com.verdenroz.verdaxmarket.core.data.utils.NetworkMonitor
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
    internal abstract fun bindsWatchlistRepository(
        watchlistRepository: ImplWatchlistRepository,
    ): WatchlistRepository

    @Binds
    internal abstract fun bindsRecentSearchRepository(
        recentSearchRepository: ImplRecentSearchRepository,
    ): RecentSearchRepository

    @Binds
    internal abstract fun bindsSignalRepository(
        signalRepository: ImplSignalRepository,
    ): SignalRepository

    @Binds
    internal abstract fun bindsSocketRepository(
        socketMonitor: ImplSocketRepository,
    ): SocketRepository

    @Binds
    internal abstract fun bindsUserDataRepository(
        userDataRepository: ImplUserDataRepository,
    ): UserDataRepository

    @Binds
    internal abstract fun bindsNetworkMonitor(
        networkMonitor: ConnectivityManagerNetworkMonitor,
    ): NetworkMonitor

    @Binds
    internal abstract fun bindsMarketMonitor(
        marketMonitor: MarketStatusMonitor,
    ): MarketMonitor

}
