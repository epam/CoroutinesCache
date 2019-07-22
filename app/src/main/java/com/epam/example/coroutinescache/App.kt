package com.epam.example.coroutinescache

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            //modules(listOf(actionsModule, cacheModule))
        }
    }
}