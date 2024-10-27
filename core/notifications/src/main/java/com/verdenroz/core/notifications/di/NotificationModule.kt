package com.verdenroz.core.notifications.di

import android.content.Context
import com.verdenroz.core.datastore.UserSettingsStore
import com.verdenroz.core.notifications.NotificationManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object NotificationModule {
    @Provides
    fun providesNotificationManager(
        @ApplicationContext context: Context,
        userSettingsStore: UserSettingsStore,
    ): NotificationManager = NotificationManager(
        context = context,
        userSettingsStore = userSettingsStore
    )
}