package com.verdenroz.core.logging.di

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.verdenroz.core.logging.ErrorReporter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object LogModule {

    @Provides
    fun provideFirebaseCrashlytics(): FirebaseCrashlytics {
        return FirebaseCrashlytics.getInstance()
    }

    @Provides
    fun provideCrashReporter(crashlytics: FirebaseCrashlytics): ErrorReporter {
        return ErrorReporter(crashlytics)
    }
}