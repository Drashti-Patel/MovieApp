package com.movie.marvel.api

data class ApiResponse<T>(
    val code: Int? = null,
    val status: String? = null,
    val data: ApiData<T>? = null
)

data class ApiData<T>(
    val offset: Int? = null,
    val limit: Int? = null,
    val total: Int? = null,
    val count: Int? = null,
    val results: List<T>? = null
)