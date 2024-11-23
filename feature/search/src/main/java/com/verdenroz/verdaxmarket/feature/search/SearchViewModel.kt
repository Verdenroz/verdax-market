package com.verdenroz.verdaxmarket.feature.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algolia.instantsearch.searcher.hits.HitsSearcher
import com.algolia.search.model.APIKey
import com.algolia.search.model.ApplicationID
import com.algolia.search.model.IndexName
import com.algolia.search.model.search.Query
import com.verdenroz.verdaxmarket.core.data.repository.RecentSearchRepository
import com.verdenroz.verdaxmarket.core.data.repository.WatchlistRepository
import com.verdenroz.verdaxmarket.core.model.RecentQuoteResult
import com.verdenroz.verdaxmarket.core.model.RecentSearchQuery
import com.verdenroz.verdaxmarket.core.model.RegionFilter
import com.verdenroz.verdaxmarket.core.model.TypeFilter
import com.verdenroz.verdaxmarket.core.network.model.SearchResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val SEARCH_QUERY = "search_query"

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val watchlistRepository: WatchlistRepository,
    private val recentSearchRepository: RecentSearchRepository
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
        applicationID = ApplicationID(BuildConfig.ALGOLIA_APP_ID),
        apiKey = APIKey(BuildConfig.ALGOLIA_API_KEY),
        indexName = IndexName("stocks"),
        query = searchQuery.value
    )

    private val query: StateFlow<String> =
        savedStateHandle.getStateFlow(key = SEARCH_QUERY, initialValue = "")

    private val savedSearchResults: MutableStateFlow<List<SearchResult>> = MutableStateFlow(emptyList())
    val searchResults: MutableStateFlow<List<SearchResult>> = MutableStateFlow(emptyList())

    val recentQueries: StateFlow<List<RecentSearchQuery>> =
        recentSearchRepository.getRecentSearchQueries(10).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    val recentQuotes: StateFlow<List<RecentQuoteResult>> =
        recentSearchRepository.getRecentQuotes(10).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    val resultsInWatchlist: StateFlow<List<Boolean>> = combine(
        searchResults,
        watchlistRepository.watchlist
    ) { results, watchlist ->
        results.map { result -> watchlist.any { it == result.symbol } }
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

    /**
     * Called when the search action is explicitly triggered by the user. For example, when the
     * IME Action is Search or when the enter key is pressed in the search text field.
     */
    fun onSearch(query: String) {
        if (query.isBlank() || this.query.value.isBlank()) {
            searchResults.value = emptyList()
        } else {
            searcher.setQuery(query)
            searcher.searchAsync()

            viewModelScope.launch {
                recentSearchRepository.upsertRecentQuery(query)
            }
        }
    }

    fun updateQuery(query: String) {
        savedStateHandle[SEARCH_QUERY] = query
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

    fun clearSearchResults() {
        savedSearchResults.value = searchResults.value
        searchResults.value = emptyList()
    }

    fun restoreSearchResults() {
        searchResults.value = savedSearchResults.value
    }

    fun addToWatchlist(searchResult: SearchResult) {
        viewModelScope.launch { watchlistRepository.addToWatchList(searchResult.symbol) }
    }

    fun deleteFromWatchlist(searchResult: SearchResult) {
        viewModelScope.launch { watchlistRepository.deleteFromWatchList(searchResult.symbol) }
    }

    fun removeRecentQuery(query: RecentSearchQuery) {
        viewModelScope.launch { recentSearchRepository.deleteRecentQuery(query) }
    }

    fun removeRecentQuote(quote: RecentQuoteResult) {
        viewModelScope.launch { recentSearchRepository.deleteRecentQuote(quote) }
    }

    fun clearRecentQueries() {
        viewModelScope.launch { recentSearchRepository.clearRecentQueries() }
    }

    fun clearRecentQuotes() {
        viewModelScope.launch { recentSearchRepository.clearRecentQuotes() }
    }

}