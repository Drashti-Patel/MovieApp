package com.movie.marvel.di

import com.movie.marvel.api.MovieApi
import com.movie.marvel.movies.MovieListRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class RepositoryModule {

    @ExperimentalCoroutinesApi
    @Singleton
    @Provides
    fun provideMovieRepository(
        api: MovieApi
    ): MovieListRepository {
        return MovieListRepository(api)
    }
}