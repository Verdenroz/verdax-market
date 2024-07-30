package com.verdenroz.verdaxmarket.core.database.di


import com.verdenroz.verdaxmarket.core.database.VxmDatabase
import com.verdenroz.verdaxmarket.core.database.dao.QuoteDao
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
