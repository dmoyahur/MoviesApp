package com.dmoyahur.moviesapp.feature.movies

import androidx.compose.ui.test.junit4.createComposeRule
import com.dmoyahur.moviesapp.common.ui.components.Screen
import com.dmoyahur.moviesapp.feature.movies.ui.MoviesScreen
import com.dmoyahur.moviesapp.feature.movies.ui.MoviesUiState
import com.dmoyahur.moviesapp.model.error.AsyncException
import com.dmoyahur.moviesapp.testShared.MovieMock
import com.dmoyahur.moviesapp.testShared.utils.assertTextIsDisplayed
import org.junit.Rule
import org.junit.Test
import com.dmoyahur.moviesapp.common.R as commonRes

class MoviesUnitTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun givenAListOfPopularMovies_PopularMoviesAreShown(): Unit = with(composeTestRule) {
        val movies = MovieMock.movies
        val state = MoviesUiState.Success(movies = movies)
        setContent {
            Screen {
                MoviesScreen(state) {}
            }
        }

        assertTextIsDisplayed(movies[0].title)
        assertTextIsDisplayed(movies[1].title)
        assertTextIsDisplayed(movies[2].title)
    }

    @Test
    fun givenAnError_ErrorScreenIsShown(): Unit = with(composeTestRule) {
        val error = AsyncException.UnknownError("Unknown error", Exception())
        setContent {
            Screen {
                MoviesScreen(state = MoviesUiState.Error(error), onMovieClick = {})
            }
        }

        assertTextIsDisplayed(commonRes.string.generic_error)
    }
}