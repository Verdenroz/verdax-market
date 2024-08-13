package com.verdenroz.verdaxmarket.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.verdenroz.verdaxmarket.core.database.dao.QuoteDao
import com.verdenroz.verdaxmarket.core.database.dao.RecentQuoteDao
import com.verdenroz.verdaxmarket.core.database.dao.RecentSearchDao
import com.verdenroz.verdaxmarket.core.database.model.QuoteEntity
import com.verdenroz.verdaxmarket.core.database.model.RecentQuoteEntity
import com.verdenroz.verdaxmarket.core.database.model.RecentSearchEntity
import com.verdenroz.verdaxmarket.core.database.util.InstantTypeConverter

@Database(
    entities = [
        QuoteEntity::class,
        RecentQuoteEntity::class,
        RecentSearchEntity::class
    ],
    version = 1
)
@TypeConverters(
    InstantTypeConverter::class,
)
internal abstract class VxmDatabase : RoomDatabase() {
    abstract fun quoteDao(): QuoteDao

    abstract fun recentQuoteDao(): RecentQuoteDao

    abstract fun recentSearchDao(): RecentSearchDao
}
