package com.movie.marvel.movies

import com.movie.marvel.api.ApiClient
import com.movie.marvel.api.ApiResponse
import com.movie.marvel.movies.model.Movies
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class MovieListRepository (private val apiClient: ApiClient) {

    fun getCharacters(offset: Int,searchParam: String? = null): Flow<ApiResponse<Movies>> = flow {
        emit(
            apiClient.getCharacters(offset,searchParam)
        )
    }.flowOn(Dispatchers.IO)

    fun getComics(offset: Int, filterByDate: String? = null): Flow<ApiResponse<Movies>> = flow {
        emit(
            apiClient.getComics(offset,filterByDate)
        )
    }.flowOn(Dispatchers.IO)
}