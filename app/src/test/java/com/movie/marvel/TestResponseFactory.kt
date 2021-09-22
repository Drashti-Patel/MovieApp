package com.movie.marvel

import com.google.gson.Gson
import java.io.BufferedReader
import java.lang.reflect.Type

object TestResponseFactory {
    fun jsonFileToResponse(filename: String, typeToken: Type): Any {
        val classLoader = javaClass.classLoader
        val stream = classLoader.getResourceAsStream(filename)
        val bufferedReader: BufferedReader = stream.bufferedReader()
        val inputString = bufferedReader.use { it.readText() }
        return Gson().fromJson(inputString, typeToken)
    }
}