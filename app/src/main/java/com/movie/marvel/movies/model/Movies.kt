package com.movie.marvel.movies.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Movies(
    @SerializedName("id") val id: Int,
    @SerializedName("thumbnail") val thumbnail: Thumbnail,
    @SerializedName("name") val name: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("title") val title: String?
) : Parcelable

@Parcelize
data class Thumbnail(
    @SerializedName("path") val path: String,
    @SerializedName("extension") val extension: String
) : Parcelable {
    fun getUrl() = "$path.$extension".replace("http://", "https://")
}

enum class FilterComicsByDate {
    LAST_WEEK,
    THIS_WEEK,
    NEXT_WEEK,
    THIS_MONTH
}