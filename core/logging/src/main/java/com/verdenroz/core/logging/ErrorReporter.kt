package com.verdenroz.core.logging

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.verdenroz.verdaxmarket.core.common.error.DataError
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ErrorReporter @Inject constructor(
    private val crashlytics: FirebaseCrashlytics
) {

    /**
     * Unified logging method for all error types
     * @param error The [DataError] enum that occurred
     * @param exception The actual exception that occurred
     * @param metadata Map of additional context information to log
     */
    fun logError(
        error: DataError,
        exception: Throwable,
        metadata: Map<String, String> = emptyMap()
    ) {
        // Don't log input errors
        if (error is DataError.Input) {
            return
        }

        // For auth errors, only log specific critical failures
        if (error is DataError.Auth) {
            val shouldLog = when (error) {
                // Critical auth errors we want to track
                DataError.Auth.SIGN_OUT_FAILED,
                DataError.Auth.ACCOUNT_DELETE_FAILED,
                DataError.Auth.GOOGLE_SIGN_IN_FAILED,
                DataError.Auth.GITHUB_SIGN_IN_FAILED,
                DataError.Auth.PASSWORD_RESET_FAILED,
                DataError.Auth.GOOGLE_PLAY_SERVICES_UNAVAILABLE,
                DataError.Auth.AUTH_UNKNOWN_ERROR -> true

                // Common auth errors we don't need to track
                DataError.Auth.INVALID_CREDENTIALS,
                DataError.Auth.USER_NOT_FOUND,
                DataError.Auth.EMAIL_ALREADY_EXISTS,
                DataError.Auth.NO_SAVED_CREDENTIALS -> false
            }

            if (!shouldLog) {
                return
            }
        }

        crashlytics.apply {
            setCustomKey("error_type", error.toName())
            metadata.forEach { (key, value) ->
                setCustomKey(key, value)
            }
            recordException(exception)
        }
    }


    private fun DataError.toName(): String {
        return (this as Enum<*>).name
    }

}