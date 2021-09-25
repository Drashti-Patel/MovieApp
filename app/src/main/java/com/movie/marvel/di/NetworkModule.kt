package com.movie.marvel.di

import com.movie.marvel.InternalConfig
import com.movie.marvel.api.MovieApi
import com.movie.marvel.api.MovieApi.Companion.BASE_URL
import com.movie.marvel.utils.md5
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import kotlin.math.abs
import kotlin.random.Random

@Module
@InstallIn(ApplicationComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun provideMovieApi(): MovieApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(provideOkhttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MovieApi::class.java)
    }

    @Provides
    fun provideOkhttpClient(): OkHttpClient {

        val httpClient = OkHttpClient.Builder()

        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        httpClient.addInterceptor(getInterceptor())

        httpClient.interceptors().add(Interceptor { chain ->
            val request = chain.request()
            val response = chain.proceed(request)
            response
        })

        return httpClient.addInterceptor(interceptor)
            .connectTimeout(1, TimeUnit.MINUTES)
            .writeTimeout(1, TimeUnit.MINUTES)
            .readTimeout(1, TimeUnit.MINUTES)
            .build()
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

    companion object {
        const val PARAM_TS = "ts"
        const val PARAM_HASH = "hash"
        const val PARAM_API_KEY = "apikey"
    }
}