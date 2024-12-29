package com.verdenroz.core.sync.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.verdenroz.core.datastore.UserSettingsStore
import com.verdenroz.core.sync.SyncManager
import com.verdenroz.verdaxmarket.core.common.dispatchers.Dispatcher
import com.verdenroz.verdaxmarket.core.common.dispatchers.FinanceQueryDispatchers
import com.verdenroz.verdaxmarket.core.common.dispatchers.di.ApplicationScope
import com.verdenroz.verdaxmarket.core.data.repository.WatchlistRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope

@Module
@InstallIn(SingletonComponent::class)
object SyncModule {

    @Provides
    fun providesFirebaseFirestore() = FirebaseFirestore.getInstance()

    @Provides
    fun providesFirebaseAuth() = FirebaseAuth.getInstance()

    @Provides
    fun providesSyncManager(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth,
        userSettingsStore: UserSettingsStore,
        watchlistRepository: WatchlistRepository,
        @Dispatcher(FinanceQueryDispatchers.IO) ioDispatcher: CoroutineDispatcher,
        @ApplicationScope applicationScope: CoroutineScope
    ): SyncManager {
        return SyncManager(firestore, auth, userSettingsStore, watchlistRepository, ioDispatcher, applicationScope)
    }
}