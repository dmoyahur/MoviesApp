package com.dmoyahur.moviesapp.feature.search.ui

import com.dmoyahur.core.model.MovieBo

data class SearchUiState(
    val query: String = "",
    val movies: List<MovieBo> = emptyList(),
    val previousSearches: List<MovieBo> = emptyList(),
    val error: Throwable? = null,
    val loading: Boolean = false
)