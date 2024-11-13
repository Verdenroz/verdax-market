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

    enum class Local: DataError {
        BLANK_EMAIL,
        INVALID_EMAIL,
        BLANK_PASSWORD,
        INVALID_PASSWORD,
    }

}



