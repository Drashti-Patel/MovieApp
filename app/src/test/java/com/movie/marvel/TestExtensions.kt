package com.movie.marvel

import org.mockito.Mockito

object TestExtensions {
    inline fun <reified T : Any> mock(): T = Mockito.mock(T::class.java)
}