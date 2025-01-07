package com.verdenroz.verdaxmarket.feature.search

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algolia.instantsearch.searcher.hits.HitsSearcher
import com.algolia.search.client.ClientSearch
import com.algolia.search.helper.toAttribute
import com.algolia.search.model.APIKey
import com.algolia.search.model.ApplicationID
import com.algolia.search.model.IndexName
import com.algolia.search.model.ObjectID
import com.algolia.search.model.indexing.Partial
import com.algolia.search.model.search.Query
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.verdenroz.verdaxmarket.core.common.result.Result
import com.verdenroz.verdaxmarket.core.data.repository.RecentSearchRepository
import com.verdenroz.verdaxmarket.core.data.repository.UserDataRepository
import com.verdenroz.verdaxmarket.core.data.repository.WatchlistRepository
import com.verdenroz.verdaxmarket.core.designsystem.util.asUiText
import com.verdenroz.verdaxmarket.core.model.RegionFilter
import com.verdenroz.verdaxmarket.core.model.SimpleQuoteData
import com.verdenroz.verdaxmarket.core.model.TypeFilter
import com.verdenroz.verdaxmarket.core.network.model.SearchResult
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val SEARCH_QUERY = "search_query"

sealed interface SearchState {
    object Loading : SearchState
    data class Success(val recentQuotes: List<SimpleQuoteData>) : SearchState
}

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val watchlistRepository: WatchlistRepository,
    private val recentSearchRepository: RecentSearchRepository,
    private val firebaseCrashlytics: FirebaseCrashlytics,
    userDataRepository: UserDataRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _regionFilter = MutableStateFlow(RegionFilter.US)
    val regionFilter = _regionFilter.asStateFlow()

    private val _typeFilter: MutableStateFlow<List<TypeFilter>> =
        MutableStateFlow(listOf(TypeFilter.STOCK, TypeFilter.ETF, TypeFilter.TRUST))
    val typeFilter = _typeFilter.asStateFlow()

    private val client = ClientSearch(
        applicationID = ApplicationID(BuildConfig.ALGOLIA_APP_ID),
        apiKey = APIKey(BuildConfig.ALGOLIA_API_KEY)
    )
    private val index = client.initIndex(IndexName("stocks"))

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

    private val savedSearchResults: MutableStateFlow<List<SearchResult>> =
        MutableStateFlow(emptyList())
    val searchResults: MutableStateFlow<List<SearchResult>> = MutableStateFlow(emptyList())

    val recentQueries: StateFlow<List<String>> =
        recentSearchRepository.getRecentSearchQueries(15).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    private val errorChannel = Channel<String>()
    val searchErrors = errorChannel.receiveAsFlow()

    val searchState = recentSearchRepository.recentQuotes.map { quotes ->
        when (quotes) {
            is Result.Success -> SearchState.Success(quotes.data)
            is Result.Error -> {
                val errorMessage = quotes.error.asUiText().asString(context)
                errorChannel.send(errorMessage)
                SearchState.Loading
            }

            is Result.Loading -> SearchState.Loading
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        SearchState.Loading
    )

    val recentSymbolsNames: StateFlow<List<Triple<String, String, String?>>> =
        recentSearchRepository.recentSymbolsNames.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            emptyList()
        )

    val resultsInWatchlist: StateFlow<Map<String, Boolean>> = combine(
        searchResults,
        watchlistRepository.watchlist
    ) { symbols, watchlist ->
        symbols.associate { (symbol, _) -> symbol to watchlist.any { it.symbol == symbol } }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L),
        emptyMap()
    )

    val recentQuotesInWatchlist: StateFlow<Map<String, Boolean>> = combine(
        recentSymbolsNames,
        watchlistRepository.watchlist
    ) { symbols, watchlist ->
        symbols.associate { (symbol, _) -> symbol to watchlist.any { it.symbol == symbol } }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L),
        emptyMap()
    )

    init {
        // Sort the search results by type and then by views
        // Prioritizing stocks, then ETFs, and finally trusts
        searcher.response.subscribe { response ->
            searchResults.value = response?.hits?.take(10)?.mapNotNull { hit ->
                hit.deserialize(SearchResult.serializer()).takeIf { it.name.isNotBlank() }
            }
                ?.sortedWith(compareBy<SearchResult> { TypeFilter.entries.find { filter -> filter.type == it.type }?.ordinal }
                    .thenByDescending { it.views ?: 0 }
                ) ?: emptyList()
        }

        viewModelScope.launch {
            userDataRepository.userSetting.collect { userSetting ->
                _regionFilter.value = userSetting.regionPreference
            }
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

    fun onClick(result: SearchResult) {
        viewModelScope.launch {
            try {
                recentSearchRepository.upsertRecentQuery(query.value)
                val viewCountAttribute = "views".toAttribute()
                val partial = Partial.Increment(viewCountAttribute, 1)
                index.partialUpdateObject(
                    objectID = ObjectID(result.objectID),
                    partial = partial
                )
            } catch (e: Exception) {
                // Silently fail
                firebaseCrashlytics.apply {
                    setCustomKey("objectID", result.objectID)
                    recordException(e)
                }
            }
        }
    }

    fun updateQuery(query: String) {
        savedStateHandle[SEARCH_QUERY] = query
    }

    fun updateTypeFilter(type: TypeFilter) {
        if (!typeFilter.value.contains(type)) {
            _typeFilter.value += type
        } else {
            _typeFilter.value -= type
        }

        searcher.query.facetFilters = (listOf(
            regionFilter.value.exchanges.map { "exchangeShortName:$it" },
            typeFilter.value.map { "type:${it.type}" }
        ))

        searcher.searchAsync()
    }

    fun updateRegionFilter(region: RegionFilter) {
        _regionFilter.value = region

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

    fun addToWatchlist(symbol: String, name: String, logo: String?) {
        viewModelScope.launch {
            watchlistRepository.addToWatchList(symbol, name, logo)
        }
    }

    fun deleteFromWatchlist(symbol: String) {
        viewModelScope.launch {
            watchlistRepository.deleteFromWatchList(symbol)
        }
    }

    fun removeRecentQuery(query: String) {
        viewModelScope.launch { recentSearchRepository.deleteRecentQuery(query) }
    }

    fun removeRecentQuote(symbol: String) {
        viewModelScope.launch { recentSearchRepository.deleteRecentQuote(symbol) }
    }

    fun clearRecentQueries() {
        viewModelScope.launch { recentSearchRepository.clearRecentQueries() }
    }

    fun clearRecentQuotes() {
        viewModelScope.launch { recentSearchRepository.clearRecentQuotes() }
    }

}