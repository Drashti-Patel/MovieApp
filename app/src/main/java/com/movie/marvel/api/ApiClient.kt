package com.movie.marvel.api

import androidx.viewbinding.BuildConfig
import com.movie.marvel.InternalConfig
import com.movie.marvel.movies.model.Movies
import com.movie.marvel.utils.ErrorCodes
import com.movie.marvel.utils.md5
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor
import java.net.UnknownHostException
import java.util.*
import kotlin.math.abs
import kotlin.random.Random

class ApiClient {
    private val client = HttpClient(OkHttp)
    {
        engine {
            config {
                retryOnConnectionFailure(true)
            }
            addInterceptor(getInterceptor())
            if (BuildConfig.DEBUG) {
                val logging = HttpLoggingInterceptor()
                logging.level = HttpLoggingInterceptor.Level.BODY
                addInterceptor(logging)
            }
        }
        install(JsonFeature) {
            serializer = GsonSerializer()
        }
        install(HttpTimeout) {
            // timeout config
            socketTimeoutMillis = HttpTimeout.INFINITE_TIMEOUT_MS
        }
        expectSuccess = false
    }

    private fun getInterceptor(): Interceptor {
        val tsId = getTsId()
        return Interceptor { chain ->
            var request = chain.request()
            val url = request.url.newBuilder()
                .addQueryParameter(PARAM_TS, tsId)
                .addQueryParameter(PARAM_HASH, getHash(tsId))
                .addQueryParameter(PARAM_API_KEY, InternalConfig.PUBLIC_KEY)
                .build()
            request = request.newBuilder()
                .url(url).build()
            chain.proceed(request)
        }
    }

    private fun getTsId() = abs(Random.nextLong()).toString()
    private fun getHash(tsId: String): String {
        return (tsId + InternalConfig.PRIVATE_KEY + InternalConfig.PUBLIC_KEY).md5()
    }

    suspend fun getCharacters(offset: Int, nameStartWith: String?): ApiResponse<Movies> {
        return try {
            val url = ApiEndPoints.CHARACTERS_URL
            client.get(url) {
                parameter(PARAM_OFFSET, offset)
                if(nameStartWith != null) parameter(PARAM_SEARCH,
                    nameStartWith.toLowerCase(Locale.getDefault()))
            }
        } catch (ex: Exception) {
            processException(ex)
        }
    }

    suspend fun getComics(offset: Int, dateDescriptor: String? = null): ApiResponse<Movies> {
        return try {
            val url = ApiEndPoints.COMICS_URL
            client.get(url) {
                parameter(PARAM_OFFSET, offset)
                if(dateDescriptor != null) parameter(PARAM_DATE_DESCRIPTOR, dateDescriptor)
            }
        } catch (ex: Exception) {
            processException(ex)
        }
    }

    private fun <T : Any> processException(ex: Exception): ApiResponse<T> {
        ex.printStackTrace()
        return if (ex is UnknownHostException)
            ApiResponse(code = ErrorCodes.NO_INTERNET.value, status = ex.message)
        else
            ApiResponse(status = ex.message)
    }

    companion object {
        const val PARAM_TS = "ts"
        const val PARAM_HASH = "hash"
        const val PARAM_API_KEY = "apikey"
        const val PARAM_OFFSET = "offset"
        const val PARAM_SEARCH = "nameStartsWith"
        const val PARAM_DATE_DESCRIPTOR = "dateDescriptor"
    }
}