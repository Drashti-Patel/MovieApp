package com.movie.marvel.movies.characters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.movie.marvel.api.ApiClient
import com.movie.marvel.movies.MovieListRepository

class CharacterViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CharacterListViewModel::class.java)) {
            return CharacterListViewModel(
                movieListRepo = MovieListRepository(
                    apiClient = ApiClient()
                )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}