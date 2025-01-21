package com.verdenroz.verdaxmarket.core.common.error


/**
 * Represents an error during execution
 */
sealed interface Error


/**
 * Represents an error that occurred during data retrieval.
 */
sealed interface DataError : Error {

    enum class Network : DataError {
        NO_INTERNET,
        TIMEOUT,
        BAD_REQUEST,
        DENIED,
        NOT_FOUND,
        THROTTLED,
        SERVER_DOWN,
        UNKNOWN,
    }

    enum class Local : DataError {
        //Input errors
        BLANK_EMAIL,
        INVALID_EMAIL,
        BLANK_PASSWORD,
        INVALID_PASSWORD,
        CONFIRM_PASSWORD_MISMATCH,

        // Auth errors
        INVALID_CREDENTIALS,
        USER_NOT_FOUND,
        EMAIL_ALREADY_EXISTS,
        NO_SAVED_CREDENTIALS,
        GOOGLE_SIGN_IN_FAILED,
        GITHUB_SIGN_IN_FAILED,
        PASSWORD_RESET_FAILED,
        SIGN_OUT_FAILED,
        ACCOUNT_DELETE_FAILED,
        GOOGLE_PLAY_SERVICES_UNAVAILABLE,
        REAUTH_REQUIRED,
        AUTH_UNKNOWN_ERROR
    }

}



