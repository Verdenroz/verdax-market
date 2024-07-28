package com.verdenroz.verdaxmarket.database.di

import android.content.Context
import androidx.room.Room
import com.verdenroz.verdaxmarket.database.VxmDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {
    @Provides
    @Singleton
    fun providesVxmDatabase(
        @ApplicationContext context: Context,
    ): VxmDatabase = Room.databaseBuilder(
        context,
        VxmDatabase::class.java,
        "vxm-database",
    ).build()
}
