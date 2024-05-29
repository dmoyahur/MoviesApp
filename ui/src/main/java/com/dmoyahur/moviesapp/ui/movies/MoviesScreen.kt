package com.dmoyahur.moviesapp.ui.movies

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.dmoyahur.moviesapp.domain.model.MovieBo
import com.dmoyahur.ui.R
import com.dmoyahur.moviesapp.ui.common.DefaultTopBar
import com.dmoyahur.moviesapp.ui.common.ErrorScreen
import com.dmoyahur.moviesapp.ui.common.LoadingIndicator
import com.dmoyahur.moviesapp.ui.common.Screen
import java.util.Date
import kotlin.random.Random

@Composable
fun MoviesRoute(viewModel: MoviesViewModel, onMovieClick: (MovieBo) -> Unit) {
    val state by viewModel.state.collectAsStateWithLifecycle(
        lifecycleOwner = LocalLifecycleOwner.current
    )

    if (state.error != null) {
        ErrorScreen(error = state.error)
    } else {
        MoviesScreen(state = state, onMovieClick = onMovieClick)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MoviesScreen(state: MoviesUiState, onMovieClick: (MovieBo) -> Unit) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Screen {
        Scaffold(
            topBar = {
                DefaultTopBar(
                    title = stringResource(id = R.string.movies),
                    scrollBehavior = scrollBehavior
                )
            },
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
        ) { padding ->
            MoviesList(
                movies = state.movies,
                contentPadding = padding,
                onMovieClick = onMovieClick,
                modifier = Modifier.padding(horizontal = 4.dp)
            )

            if (state.loading) {
                LoadingIndicator()
            }
        }
    }
}

@Composable
private fun MoviesList(
    movies: List<MovieBo>,
    contentPadding: PaddingValues,
    onMovieClick: (MovieBo) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(120.dp),
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        items(movies, key = { it.id }) {
            MovieItem(movie = it) { onMovieClick(it) }
        }
    }
}

@Composable
private fun MovieItem(movie: MovieBo, onClick: () -> Unit) {
    Column(modifier = Modifier.clickable(onClick = onClick)) {
        AsyncImage(
            model = movie.poster,
            contentDescription = movie.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2 / 3f)
                .clip(MaterialTheme.shapes.small)
        )
        Text(
            text = movie.title,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MoviesScreenPreview() {
    MoviesScreen(
        state = MoviesUiState(
            movies = (1..100).map {
                MovieBo(
                    id = it,
                    title = "Movie $it",
                    overview = "Overview $it",
                    popularity = Random.nextDouble(0.0, 10_000.0),
                    releaseDate = Date().toString(),
                    poster = "https://picsum.photos/200/300?id=$it",
                    backdrop = null,
                    originalTitle = "Movie $it",
                    originalLanguage = "en",
                    voteAverage = it / 10.0
                )
            }),
        onMovieClick = {}
    )
}