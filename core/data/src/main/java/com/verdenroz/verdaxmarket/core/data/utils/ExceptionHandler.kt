package com.verdenroz.verdaxmarket.core.data.utils

import com.verdenroz.verdaxmarket.core.common.error.DataError
import com.verdenroz.verdaxmarket.core.common.error.DataException
import com.verdenroz.verdaxmarket.core.common.error.HttpException
import com.verdenroz.verdaxmarket.core.common.error.NetworkException

fun handleNetworkException(e: Throwable): DataError.Network {
    return when (e) {
        is DataException -> {
            when (e) {
                is HttpException -> {
                    when (e.code) {
                        400 -> DataError.Network.BAD_REQUEST
                        401, 403 -> DataError.Network.DENIED
                        404 -> DataError.Network.NOT_FOUND
                        408 -> DataError.Network.TIMEOUT
                        429 -> DataError.Network.THROTTLED
                        500, 504 -> DataError.Network.SERVER_DOWN
                        else -> DataError.Network.UNKNOWN
                    }
                }

                is NetworkException -> DataError.Network.NO_INTERNET
                else -> DataError.Network.UNKNOWN
            }
        }
        else -> DataError.Network.UNKNOWN
    }
}