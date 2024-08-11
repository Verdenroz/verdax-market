package com.verdenroz.verdaxmarket.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algolia.instantsearch.searcher.hits.HitsSearcher
import com.algolia.search.model.APIKey
import com.algolia.search.model.ApplicationID
import com.algolia.search.model.IndexName
import com.algolia.search.model.search.Query
import com.verdenroz.verdaxmarket.core.common.result.Result
import com.verdenroz.verdaxmarket.core.data.repository.WatchlistRepository
import com.verdenroz.verdaxmarket.core.model.RegionFilter
import com.verdenroz.verdaxmarket.core.model.TypeFilter
import com.verdenroz.verdaxmarket.core.network.model.SearchResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val watchlistRepository: WatchlistRepository
) : ViewModel() {

    val regionFilter = MutableStateFlow(RegionFilter.US)

    val typeFilter: MutableStateFlow<List<TypeFilter>> =
        MutableStateFlow(listOf(TypeFilter.STOCK, TypeFilter.ETF, TypeFilter.TRUST))

    private val searchQuery: StateFlow<Query> = MutableStateFlow(Query(
        hitsPerPage = 10,
        facetFilters = (listOf(
            regionFilter.value.exchanges.map { "exchangeShortName:$it" },
            typeFilter.value.map { "type:${it.type}" }
        ))))

    private val searcher = HitsSearcher(
        applicationID = ApplicationID(BuildConfig.algoliaAppID),
        apiKey = APIKey(BuildConfig.algoliaAPIKey),
        indexName = IndexName("stocks"),
        query = searchQuery.value
    )

    private val query: MutableStateFlow<String> = MutableStateFlow("")

    val searchResults: MutableStateFlow<List<SearchResult>> = MutableStateFlow(emptyList())

    val resultsInWatchlist: StateFlow<List<Boolean>> = combine(
        searchResults,
        watchlistRepository.watchlist
    ) { results, watchlist ->
        if (watchlist is Result.Success) {
            results.map { result ->
                watchlistRepository.isSymbolInWatchlist(result.symbol).first()
            }
        } else {
            results.map { false }
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        emptyList()
    )

    init {
        searcher.response.subscribe { response ->
            searchResults.value = response?.hits?.take(5)?.mapNotNull { hit ->
                hit.deserialize(SearchResult.serializer()).takeIf { it.name.isNotBlank() }
            } ?: emptyList()
        }
    }

    override fun onCleared() {
        super.onCleared()
        searcher.cancel()
    }

    fun search(query: String) {
        if (query.isEmpty() || this.query.value.isEmpty()) {
            searchResults.value = emptyList()
        } else {
            searcher.setQuery(query)
            searcher.searchAsync()
        }
    }

    fun updateQuery(query: String) {
        this.query.value = query
    }

    fun updateTypeFilter(type: TypeFilter) {
        if (!typeFilter.value.contains(type)) {
            typeFilter.value += type
        } else {
            typeFilter.value -= type
        }

        searcher.query.facetFilters = (listOf(
            regionFilter.value.exchanges.map { "exchangeShortName:$it" },
            typeFilter.value.map { "type:${it.type}" }
        ))

        searcher.searchAsync()
    }

    fun updateRegionFilter(region: RegionFilter) {
        regionFilter.value = region

        searcher.query.facetFilters = (listOf(
            regionFilter.value.exchanges.map { "exchangeShortName:$it" },
            typeFilter.value.map { "type:${it.type}" }
        ))

        searcher.searchAsync()
    }

    fun addToWatchlist(searchResult: SearchResult) {
        viewModelScope.launch { watchlistRepository.addToWatchList(searchResult.symbol) }
    }

    fun deleteFromWatchlist(searchResult: SearchResult) {
        viewModelScope.launch { watchlistRepository.deleteFromWatchList(searchResult.symbol) }
    }

}