package com.verdenroz.verdaxmarket.database.di


import com.verdenroz.verdaxmarket.database.VxmDatabase
import com.verdenroz.verdaxmarket.database.dao.QuoteDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object DaosModule {
    @Provides
    fun providesQuotesDao(
        database: VxmDatabase,
    ): QuoteDao = database.quoteDao()

}
