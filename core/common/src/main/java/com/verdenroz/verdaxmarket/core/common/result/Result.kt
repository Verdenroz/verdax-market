package com.verdenroz.verdaxmarket.core.common.result

import com.verdenroz.verdaxmarket.core.common.error.DataError

typealias RootError = DataError

/**
 * Represents the state of a resource that is provided to the UI from the data layer.
 */
sealed interface Result<out D, out E : RootError> {
    /**
     * Represents a successful retrieval of a resource.
     * @param data The data that was retrieved.
     */
    data class Success<out D>(val data: D) : Result<D, Nothing>

    /**
     * Represents an error that occurred during the retrieval of a resource.
     * @param error The error that occurred.
     */
    data class Error<out E : RootError>(val error: RootError) : Result<Nothing, E>

    /**
     * Represents a loading state of a resource.
     */
    data class Loading(val isLoading: Boolean = true) : Result<Nothing, Nothing>
}