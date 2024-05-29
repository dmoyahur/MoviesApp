package com.dmoyahur.moviesapp.ui

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.room.Room
import com.dmoyahur.moviesapp.data.MoviesRepository
import com.dmoyahur.moviesapp.data.local.MoviesDatabase
import com.dmoyahur.moviesapp.data.local.MoviesLocalDataSource
import com.dmoyahur.moviesapp.data.remote.MoviesRemoteDataSource
import com.dmoyahur.moviesapp.ui.common.NavArgs
import com.dmoyahur.moviesapp.ui.common.NavScreen
import com.dmoyahur.moviesapp.ui.detail.DetailRoute
import com.dmoyahur.moviesapp.ui.detail.DetailViewModel
import com.dmoyahur.moviesapp.ui.movies.MoviesRoute
import com.dmoyahur.moviesapp.ui.movies.MoviesViewModel

@Composable
fun Navigation() {
    val navController = rememberNavController()
    val appContext = LocalContext.current.applicationContext as Application
    val db by lazy {
        Room.databaseBuilder(
            appContext,
            MoviesDatabase::class.java,
            "movies.db"
        ).build()
    }
    val moviesRepository = remember {
        MoviesRepository(
            remoteDataSource = MoviesRemoteDataSource(),
            localDataSource = MoviesLocalDataSource(db.moviesDao())
        )
    }

    NavHost(navController = navController, startDestination = NavScreen.Movies.route) {
        composable(NavScreen.Movies.route) {
            MoviesRoute(
                viewModel { MoviesViewModel(moviesRepository) },
                onMovieClick = { movie ->
                    navController.navigate(NavScreen.Detail.createRoute(movie.id))
                }
            )
        }
        composable(
            route = NavScreen.Detail.route,
            arguments = listOf(navArgument(NavArgs.MovieId.key) { type = NavType.IntType })
        ) { backStackEntry ->
            val movieId = requireNotNull(backStackEntry.arguments?.getInt(NavArgs.MovieId.key))
            DetailRoute(
                viewModel { DetailViewModel(moviesRepository, movieId) },
                onBack = { navController.popBackStack() })
        }
    }
}