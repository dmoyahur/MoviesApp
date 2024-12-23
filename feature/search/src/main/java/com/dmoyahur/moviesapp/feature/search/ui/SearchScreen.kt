package com.dmoyahur.moviesapp.feature.search.ui

import android.annotation.SuppressLint
import androidx.annotation.StringRes
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dmoyahur.moviesapp.common.ui.components.ErrorScreen
import com.dmoyahur.moviesapp.common.ui.components.ImageCoil
import com.dmoyahur.moviesapp.common.ui.components.LoadingIndicator
import com.dmoyahur.moviesapp.common.ui.components.Screen
import com.dmoyahur.moviesapp.common.util.Constants.POSTER_ASPECT_RATIO
import com.dmoyahur.moviesapp.common.util.TestConstants
import com.dmoyahur.moviesapp.feature.search.R
import com.dmoyahur.moviesapp.feature.search.util.header
import com.dmoyahur.moviesapp.model.MovieBo
import kotlin.random.Random

@Composable
fun SearchRoute(
    viewModel: SearchViewModel = hiltViewModel(),
    onMovieClick: (MovieBo) -> Unit
) {
    val lifeCycleOwner = LocalLifecycleOwner.current
    val previousSearchesUiState by viewModel.previousSearchesUiState.collectAsStateWithLifecycle(
        lifeCycleOwner
    )
    val searchResultUiState by viewModel.searchResultUiState.collectAsStateWithLifecycle(
        lifeCycleOwner
    )
    val query by viewModel.query.collectAsStateWithLifecycle(lifeCycleOwner)
    val active by viewModel.active.collectAsStateWithLifecycle(lifeCycleOwner)

    SearchScreen(
        previousSearchesUiState = previousSearchesUiState,
        searchResultUiState = searchResultUiState,
        query = query,
        active = active,
        onQueryChange = { viewModel.onQueryChange(it) },
        onActiveChange = { viewModel.onActiveChange(it) },
        onMovieClick = onMovieClick,
        onMovieDelete = { viewModel.onMovieDelete(it) }
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
@VisibleForTesting
fun SearchScreen(
    previousSearchesUiState: PreviousSearchesUiState,
    searchResultUiState: SearchResultUiState,
    query: String,
    active: Boolean,
    onQueryChange: (String) -> Unit,
    onActiveChange: (Boolean) -> Unit,
    onMovieClick: (MovieBo) -> Unit,
    onMovieDelete: (MovieBo) -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            SearchTopBar(
                state = searchResultUiState,
                query = query,
                active = active,
                onQueryChange = onQueryChange,
                onActiveChange = onActiveChange,
                onMovieClick = onMovieClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    ) { padding ->
        PreviousSearchesContent(
            state = previousSearchesUiState,
            onMovieClick = onMovieClick,
            onMovieDelete = onMovieDelete,
            modifier = Modifier.padding(padding)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchTopBar(
    state: SearchResultUiState,
    query: String,
    active: Boolean,
    onQueryChange: (String) -> Unit,
    onActiveChange: (Boolean) -> Unit,
    onMovieClick: (MovieBo) -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    SearchBar(
        query = query,
        onQueryChange = { onQueryChange(it) },
        onSearch = { keyboardController?.hide() },
        active = active,
        onActiveChange = { onActiveChange(it) },
        placeholder = { Text(text = stringResource(id = R.string.search_placeholder)) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = stringResource(id = R.string.search_placeholder)
            )
        },
        trailingIcon = {
            if (active) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(id = R.string.search_clear),
                    modifier = Modifier.clickable {
                        if (query.isEmpty()) {
                            onActiveChange(false)
                        } else {
                            onQueryChange("")
                        }
                    }
                )
            }
        },
        shape = RectangleShape,
        colors = SearchBarDefaults.colors(containerColor = MaterialTheme.colorScheme.background),
        content = {
            if (active) {
                SearchContent(
                    state = state,
                    onMovieClick = onMovieClick
                )
            }
        },
        modifier = modifier
    )
}

@Composable
private fun PreviousSearchesContent(
    state: PreviousSearchesUiState,
    onMovieClick: (MovieBo) -> Unit,
    onMovieDelete: ((MovieBo) -> Unit),
    modifier: Modifier = Modifier
) {
    when (state) {
        is PreviousSearchesUiState.Error -> ErrorScreen(error = state.exception)
        PreviousSearchesUiState.Loading -> LoadingIndicator()
        is PreviousSearchesUiState.Success -> {
            if (state.isEmpty()) {
                EmptyScreen(icon = Icons.Default.Search, text = R.string.search_content_placeholder)
            } else {
                SearchList(
                    modifier = modifier.testTag(TestConstants.Search.PREVIOUS_SEARCHES_LIST_TAG),
                    title = stringResource(R.string.search_previous_searches),
                    movies = state.previousSearches,
                    onMovieClick = onMovieClick,
                    onMovieDelete = onMovieDelete,
                    contentPadding = PaddingValues(
                        start = 8.dp,
                        end = 8.dp,
                        top = 16.dp,
                        bottom = 100.dp
                    )
                )
            }
        }
    }
}

@Composable
private fun SearchContent(
    state: SearchResultUiState,
    onMovieClick: (MovieBo) -> Unit
) {
    when (state) {
        is SearchResultUiState.EmptyQuery -> EmptyScreen(
            icon = Icons.Default.Search,
            text = R.string.search_content_placeholder
        )
        is SearchResultUiState.Success -> {
            if (state.isEmpty()) {
                EmptyScreen(icon = Icons.Default.SearchOff, text = R.string.search_no_results)
            } else {
                SearchList(
                    modifier = Modifier.testTag(TestConstants.Search.SEARCH_RESULT_LIST_TAG),
                    title = stringResource(R.string.search_main_results),
                    movies = state.searchResult,
                    onMovieClick = onMovieClick,
                    contentPadding = PaddingValues(
                        start = 8.dp,
                        end = 8.dp,
                        top = 16.dp,
                        bottom = 148.dp
                    )
                )
            }
        }
        is SearchResultUiState.Error -> ErrorScreen(error = state.exception)
        SearchResultUiState.Loading -> Unit
    }
}

@Composable
private fun EmptyScreen(
    icon: ImageVector,
    @StringRes text: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, bottom = 128.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(64.dp)
            )
            Text(
                text = stringResource(id = text),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

@Composable
private fun SearchList(
    modifier: Modifier = Modifier,
    title: String,
    movies: List<MovieBo>,
    onMovieClick: (MovieBo) -> Unit,
    onMovieDelete: ((MovieBo) -> Unit)? = null,
    contentPadding: PaddingValues
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(128.dp),
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        header {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        items(movies, key = { it.id }) { item ->
            SearchItem(
                movie = item,
                onClick = { onMovieClick(item) },
                onDelete = onMovieDelete?.let { { it(item) } }
            )
        }
    }
}

@Composable
private fun SearchItem(
    movie: MovieBo,
    onClick: () -> Unit,
    onDelete: (() -> Unit)?
) {
    Column(
        modifier = Modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.wrapContentSize()) {
            ImageCoil(
                imageUrl = movie.poster,
                contentDescription = movie.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(POSTER_ASPECT_RATIO)
                    .clip(MaterialTheme.shapes.small)
            )
            onDelete?.let {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(id = R.string.search_delete),
                    modifier = Modifier
                        .padding(4.dp)
                        .size(20.dp)
                        .background(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.errorContainer
                        )
                        .align(Alignment.TopEnd)
                        .padding(2.dp)
                        .clickable { onDelete() },
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
        Text(
            text = movie.title,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PlaceholderScreenPreview() {
    EmptyScreen(icon = Icons.Default.Search, text = R.string.search_content_placeholder)
}

@Preview(showBackground = true)
@Composable
private fun EmptyScreenPreview() {
    EmptyScreen(icon = Icons.Default.SearchOff, text = R.string.search_no_results)
}

@Preview(showBackground = true)
@Composable
private fun SearchScreenPreview() {
    Screen {
        SearchScreen(
            previousSearchesUiState = PreviousSearchesUiState.Success(emptyList()),
            searchResultUiState = SearchResultUiState.Success(
                searchResult = (1..10).map {
                    MovieBo(
                        id = it,
                        title = "Movie $it",
                        overview = "Overview $it",
                        popularity = Random.nextDouble(0.0, 10_000.0),
                        releaseDate = "",
                        poster = null,
                        backdrop = null,
                        originalTitle = "Movie $it",
                        originalLanguage = "en",
                        voteAverage = it / 10.0
                    )
                }
            ),
            query = "Movie",
            active = true,
            onQueryChange = {},
            onActiveChange = {},
            onMovieClick = {},
            onMovieDelete = {}
        )
    }
}