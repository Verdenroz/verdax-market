package com.verdenroz.verdaxmarket.core.database.util

import androidx.room.TypeConverter
import kotlinx.datetime.Instant

internal class InstantTypeConverter {
    @TypeConverter
    fun longToInstant(value: Long?): Instant? =
        value?.let(Instant::fromEpochMilliseconds)

    @TypeConverter
    fun instantToLong(instant: Instant?): Long? =
        instant?.toEpochMilliseconds()
}