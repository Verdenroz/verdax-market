package com.verdenroz.verdaxmarket.core.data.utils

import com.verdenroz.core.logging.ErrorReporter
import com.verdenroz.verdaxmarket.core.common.error.DataError
import com.verdenroz.verdaxmarket.core.common.error.DataException
import com.verdenroz.verdaxmarket.core.common.error.HttpException
import com.verdenroz.verdaxmarket.core.common.error.NetworkException
import com.verdenroz.verdaxmarket.core.common.result.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Catches exceptions and emits them as [Result.Error] with [DataError.Network] type
 * @param exceptionHandler [ExceptionHandler] to handle exceptions
 */
fun <T> Flow<Result<T, DataError.Network>>.catchAndEmitError(exceptionHandler: ExceptionHandler): Flow<Result<T, DataError.Network>> =
    catch { e ->
        val error = exceptionHandler.handleNetworkException(e)
        emit(Result.Error(error))
    }

/**
 * Handles exceptions and logs them to the [ErrorReporter]
 */
@Singleton
class ExceptionHandler @Inject constructor(
    private val errorReporter: ErrorReporter
) {

    /**
     * Handles exceptions and logs them
     * @param e [Throwable] exception
     * @return [DataError.Network] error type
     */
    internal fun handleNetworkException(e: Throwable): DataError.Network {
        val error = when (e) {
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

        errorReporter.logError(
            error = error,
            exception = e,
        )

        return error
    }


}
