package com.verdenroz.verdaxmarket.domain

import com.verdenroz.verdaxmarket.core.common.dispatchers.Dispatcher
import com.verdenroz.verdaxmarket.core.common.dispatchers.FinanceQueryDispatchers
import com.verdenroz.verdaxmarket.core.common.error.DataError
import com.verdenroz.verdaxmarket.core.common.result.Result
import com.verdenroz.verdaxmarket.core.data.repository.SocketRepository
import com.verdenroz.verdaxmarket.core.data.utils.handleNetworkException
import com.verdenroz.verdaxmarket.core.model.Profile
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * A use case that gets the subscribed [Profile] for a given symbol
 */
class GetSubscribedProfileUseCase @Inject constructor(
    private val socket: SocketRepository,
    @Dispatcher(FinanceQueryDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) {

    /**
     * Returns a flow of the [Profile] for the given symbol
     * If there is an error, it will return [DataError.Network.UNKNOWN] so profile data
     * must be retrieved from the API instead
     * @param symbol the symbol of the quote to get
     */
    operator fun invoke(symbol: String): Flow<Result<Profile, DataError.Network>> =
        socket.getProfile(symbol).map { profile ->
            profile
        }.flowOn(ioDispatcher).catch { e -> handleNetworkException(e) }

}