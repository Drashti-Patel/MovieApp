package com.movie.marvel.utils

import com.movie.marvel.api.ApiError

sealed class UIResponse<out T> {
    data class Error(val error: ApiError) : UIResponse<Nothing>()
    data class Data<T>(val data: T) : UIResponse<T>()
    object Loading : UIResponse<Nothing>()

    val isLoading: Boolean
        get() = this is Loading
}