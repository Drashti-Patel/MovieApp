package com.movie.marvel.application

import android.app.Application

class MovieApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        mContext = this
    }

    companion object {
        private lateinit var mContext: MovieApplication

        fun getContext(): MovieApplication {
            return mContext
        }
    }
}