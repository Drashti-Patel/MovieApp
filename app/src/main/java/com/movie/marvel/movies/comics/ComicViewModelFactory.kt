package com.movie.marvel.movies.comics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.movie.marvel.api.ApiClient
import com.movie.marvel.movies.MovieListRepository

class ComicViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ComicListViewModel::class.java)) {
            return ComicListViewModel(
                movieListRepo = MovieListRepository(
                    apiClient = ApiClient()
                )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}