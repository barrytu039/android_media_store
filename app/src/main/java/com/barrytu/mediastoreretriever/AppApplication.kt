package com.barrytu.mediastoreretriever

import android.app.Application
import android.content.Context

class AppApplication : Application() {
    companion object {
        private lateinit var appContext : Context
        lateinit var mediaRetriever: MediaRetriever
        fun getAppContext() : Context {
            return appContext
        }
    }
    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        mediaRetriever = MediaRetriever()
    }

}