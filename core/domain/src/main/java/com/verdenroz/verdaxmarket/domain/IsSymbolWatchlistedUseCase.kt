package com.verdenroz.verdaxmarket.domain

import com.verdenroz.verdaxmarket.core.data.repository.WatchlistRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class IsSymbolWatchlistedUseCase @Inject constructor(
    private val watchlistRepository: WatchlistRepository,
) {
    operator fun invoke(symbol: String): Flow<Boolean> = watchlistRepository.isSymbolInWatchlist(symbol)
}