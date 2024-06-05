package com.dmoyahur.moviesapp.domain.detail

import com.dmoyahur.moviesapp.core.model.MovieBo
import com.dmoyahur.moviesapp.domain.movies.data.MoviesRepository
import com.dmoyahur.moviesapp.domain.movies.usecases.FindMovieByIdUseCase
import com.dmoyahur.moviesapp.domain.search.data.SearchRepository
import com.dmoyahur.moviesapp.domain.search.usecases.FindMovieSearchByIdUseCase
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class GetMovieByIdUseCaseTest {

    private val movie = MovieBo(
        id = 1,
        title = "Movie 1",
        overview = "Overview 1",
        popularity = 5_000.0,
        releaseDate = "",
        poster = null,
        backdrop = null,
        originalTitle = "Movie 1",
        originalLanguage = "en",
        voteAverage = 7.0
    )

    @Test
    fun `when invoke is called and fromSearch is false, then return movie from movies repository`() {
        val expectedMovie = movie
        val moviesRepository: MoviesRepository = mockk {
            every { findMovieById(any()) } returns flowOf(expectedMovie)
        }
        val searchRepository: SearchRepository = mockk()
        val findMovieByIdUseCase = FindMovieByIdUseCase(moviesRepository)
        val findMovieSearchByIdUseCase = FindMovieSearchByIdUseCase(searchRepository)
        val useCase = GetMovieByIdUseCase(findMovieByIdUseCase, findMovieSearchByIdUseCase)

        val movie = runBlocking { useCase(1, false).first() }

        coVerify(exactly = 0) { searchRepository.findMovieSearchById(1) }
        coVerify { moviesRepository.findMovieById(1) }
        Assert.assertEquals(expectedMovie, movie)
    }

    @Test
    fun `when invoke is called and fromSearch is true, then return movie from search repository`() {
        val expectedMovie = movie
        val moviesRepository: MoviesRepository = mockk()
        val searchRepository: SearchRepository = mockk {
            every { findMovieSearchById(any()) } returns flowOf(expectedMovie)
        }
        val findMovieByIdUseCase = FindMovieByIdUseCase(moviesRepository)
        val findMovieSearchByIdUseCase = FindMovieSearchByIdUseCase(searchRepository)
        val useCase = GetMovieByIdUseCase(findMovieByIdUseCase, findMovieSearchByIdUseCase)

        val movie = runBlocking { useCase(1, true).first() }

        coVerify { searchRepository.findMovieSearchById(1) }
        coVerify(exactly = 0) { moviesRepository.findMovieById(1) }
        Assert.assertEquals(expectedMovie, movie)
    }
}