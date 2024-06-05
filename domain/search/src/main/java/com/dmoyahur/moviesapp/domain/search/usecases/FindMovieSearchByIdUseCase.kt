package com.dmoyahur.moviesapp.domain.search.usecases

import com.dmoyahur.moviesapp.core.model.MovieBo
import com.dmoyahur.moviesapp.domain.search.data.SearchRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FindMovieSearchByIdUseCase @Inject constructor(private val repository: SearchRepository) {

    operator fun invoke(id: Int): Flow<MovieBo> = repository.findMovieSearchById(id)
}