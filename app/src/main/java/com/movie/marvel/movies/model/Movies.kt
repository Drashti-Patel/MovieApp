package com.movie.marvel.movies.model

data class Movies(
    val id: Int,
    val thumbnail: Thumbnail,
    val name: String? = null,
    val title: String? = null
)

data class Thumbnail(
    val path: String,
    val extension: String
) {
    fun getUrl() = "$path.$extension".replace("http://", "https://")
}

enum class FilterComicsByDate {
    LAST_WEEK,
    THIS_WEEK,
    NEXT_WEEK,
    THIS_MONTH
}