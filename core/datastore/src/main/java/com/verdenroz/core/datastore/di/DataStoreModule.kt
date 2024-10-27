package com.verdenroz.core.datastore.di

import android.content.Context
import androidx.datastore.core.DataMigration
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.dataStoreFile
import com.verdenroz.core.datastore.UserSettings
import com.verdenroz.core.datastore.UserSettingsSerializer
import com.verdenroz.core.datastore.copy
import com.verdenroz.verdaxmarket.core.common.dispatchers.Dispatcher
import com.verdenroz.verdaxmarket.core.common.dispatchers.FinanceQueryDispatchers
import com.verdenroz.verdaxmarket.core.common.dispatchers.di.ApplicationScope
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    internal fun providesUserDataStore(
        @ApplicationContext context: Context,
        @Dispatcher(FinanceQueryDispatchers.IO) ioDispatcher: CoroutineDispatcher,
        @ApplicationScope scope: CoroutineScope,
        userPreferencesSerializer: UserSettingsSerializer,
    ): DataStore<UserSettings> =
        DataStoreFactory.create(
            serializer = userPreferencesSerializer,
            scope = CoroutineScope(scope.coroutineContext + ioDispatcher),
            corruptionHandler = ReplaceFileCorruptionHandler { UserSettings.getDefaultInstance() },
            migrations = listOf(
                /**
                 * Workaround for setting default values for [UserSettings] fields on first launch.
                 * Proto3 does not support default values for fields, so we need to manually set them.
                 */
                object : DataMigration<UserSettings> {
                    override suspend fun shouldMigrate(currentData: UserSettings): Boolean {
                        return !currentData.isFirstLaunch
                    }

                    override suspend fun migrate(currentData: UserSettings): UserSettings {
                        return currentData.copy {
                            hintsEnabled = true
                            showMarketHours = true
                            isFirstLaunch = true
                        }
                    }

                    override suspend fun cleanUp() {}
                }
            )
        ) {
            context.dataStoreFile("user_settings.pb")
        }
}