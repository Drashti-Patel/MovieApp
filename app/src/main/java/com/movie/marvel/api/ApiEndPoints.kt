package com.movie.marvel.api

import com.movie.marvel.InternalConfig

object ApiEndPoints {
    private const val API_URL = InternalConfig.API_URL
    private const val CHARACTERS = "characters"
    private const val COMICS = "comics"
    const val COMICS_URL = API_URL + COMICS
    const val CHARACTERS_URL = API_URL + CHARACTERS
}