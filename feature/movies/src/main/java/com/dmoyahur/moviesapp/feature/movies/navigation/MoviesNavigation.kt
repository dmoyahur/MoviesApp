package com.dmoyahur.moviesapp.feature.movies.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dmoyahur.moviesapp.feature.movies.ui.MoviesRoute
import com.dmoyahur.moviesapp.model.MovieBo

const val MOVIES_ROUTE = "movies"

fun NavGraphBuilder.moviesScreen(onMovieClick: (MovieBo) -> Unit) {
    composable(MOVIES_ROUTE) {
        MoviesRoute(onMovieClick = onMovieClick)
    }
}