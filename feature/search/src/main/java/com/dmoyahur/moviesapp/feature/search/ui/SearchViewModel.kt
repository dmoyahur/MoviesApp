package com.dmoyahur.moviesapp.feature.search.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmoyahur.moviesapp.core.ui.model.Result
import com.dmoyahur.moviesapp.core.ui.model.asResult
import com.dmoyahur.moviesapp.domain.search.usecases.GetPreviousSearchesUseCase
import com.dmoyahur.moviesapp.domain.search.usecases.SearchMovieUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchMovieUseCase: SearchMovieUseCase,
    getPreviousSearchesUseCase: GetPreviousSearchesUseCase
) : ViewModel() {

    private val query = MutableStateFlow("")
    private val active = MutableStateFlow(false)

    private val previousSearchesState: StateFlow<PreviousSearchesState> =
        getPreviousSearchesUseCase()
            .asResult()
            .map { result ->
                when(result) {
                    is Result.Success -> PreviousSearchesState(previousSearches = result.data)
                    is Result.Error -> PreviousSearchesState(error = result.exception)
                    is Result.Loading -> PreviousSearchesState(loading = true)
                }
            }.stateInViewModelScope(initialValue = PreviousSearchesState(loading = true))

    private val moviesState: StateFlow<MoviesState> = query
        .flatMapLatest {
            if (it.isEmpty()) {
                flowOf(emptyList())
            } else {
                searchMovieUseCase(it)
            }
        }
        .asResult()
        .map { result ->
            when(result) {
                is Result.Success -> MoviesState(movies = result.data)
                is Result.Error -> MoviesState(error = result.exception)
                is Result.Loading -> MoviesState(loading = true)
            }
        }
        .stateInViewModelScope(initialValue = MoviesState())

    val searchUiState: StateFlow<SearchUiState> =
        combine(
            query,
            active,
            previousSearchesState,
            moviesState
        ) { query, active, previousSearches, movies ->
            SearchUiState(
                query = query,
                previousSearches = previousSearches.previousSearches,
                movies = movies.movies,
                loading = previousSearches.loading,
                active = active,
                error = when {
                    previousSearches.error != null -> previousSearches.error
                    query.isEmpty() -> null
                    movies.error != null -> movies.error
                    else -> null
                }
            )
        }.stateInViewModelScope(initialValue = SearchUiState(loading = true))

    fun onQueryChange(query: String) {
        this.query.value = query
    }

    fun onActiveChange(active: Boolean) {
        this.active.value = active
    }

    private fun <T> Flow<T>.stateInViewModelScope(initialValue: T): StateFlow<T> {
        return this.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = initialValue
        )
    }
}