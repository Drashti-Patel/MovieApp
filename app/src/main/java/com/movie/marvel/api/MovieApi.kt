package com.movie.marvel.api

import com.movie.marvel.InternalConfig
import com.movie.marvel.movies.model.Movies
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieApi {

    @GET("characters")
    suspend fun getCharacters(
        @Query(PARAM_OFFSET) offset: Int,
        @Query(PARAM_SEARCH) nameStartWith: String?
    ): ApiResponse<Movies>

    @GET("comics")
    suspend fun getComics(
        @Query(PARAM_OFFSET) offset: Int,
        @Query(PARAM_DATE_DESCRIPTOR) dateDescriptor: String?
    ): ApiResponse<Movies>

    companion object {
        const val BASE_URL = InternalConfig.API_URL
        const val PARAM_OFFSET = "offset"
        const val PARAM_SEARCH = "nameStartsWith"
        const val PARAM_DATE_DESCRIPTOR = "dateDescriptor"
    }
}