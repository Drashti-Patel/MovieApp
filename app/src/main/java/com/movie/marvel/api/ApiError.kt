package com.movie.marvel.api

data class ApiError(
    val code: String? = "",
    val status: String? = "Unknown error occurred"
)