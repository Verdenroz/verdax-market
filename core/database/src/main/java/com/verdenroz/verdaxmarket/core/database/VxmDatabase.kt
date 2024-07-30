package com.verdenroz.verdaxmarket.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.verdenroz.verdaxmarket.core.database.dao.QuoteDao
import com.verdenroz.verdaxmarket.core.database.model.QuoteEntity

@Database(
    entities = [
        QuoteEntity::class
    ],
    version = 1
)
internal abstract class VxmDatabase : RoomDatabase() {
    abstract fun quoteDao(): QuoteDao
}
