package com.verdenroz.verdaxmarket.core.data.utils

import android.database.sqlite.SQLiteException
import com.verdenroz.verdaxmarket.core.common.error.DataError
import com.verdenroz.verdaxmarket.core.common.error.DataException
import com.verdenroz.verdaxmarket.core.common.error.HttpException
import com.verdenroz.verdaxmarket.core.common.error.NetworkException
import com.verdenroz.verdaxmarket.core.common.result.Result

fun handleNetworkException(e: Throwable): Result.Error<DataError.Network> {
    return when (e) {
        is DataException -> {
            when (e) {
                is HttpException -> {
                    when (e.code) {
                        400 -> Result.Error(DataError.Network.BAD_REQUEST)
                        401, 403 -> Result.Error(DataError.Network.DENIED)
                        404 -> Result.Error(DataError.Network.NOT_FOUND)
                        408 -> Result.Error(DataError.Network.TIMEOUT)
                        429 -> Result.Error(DataError.Network.THROTTLED)
                        500, 504 -> Result.Error(DataError.Network.SERVER_DOWN)
                        else -> Result.Error(DataError.Network.UNKNOWN)
                    }
                }
                is NetworkException -> Result.Error(DataError.Network.NO_INTERNET)
                else -> Result.Error(DataError.Network.UNKNOWN)
            }
        }
        else -> Result.Error(DataError.Network.UNKNOWN)
    }
}

fun handleLocalException(e: Throwable): Result.Error<DataError.Local> {
    return when (e) {
        is SQLiteException -> {
            Result.Error(DataError.Local.DATABASE)
        }

        else -> Result.Error(DataError.Local.UNKNOWN)
    }
}