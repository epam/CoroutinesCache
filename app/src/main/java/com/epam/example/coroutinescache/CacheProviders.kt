package com.epam.example.coroutinescache

import com.epam.coroutinecache.annotations.*
import kotlinx.coroutines.Deferred
import java.util.concurrent.TimeUnit

interface CacheProviders {

    @ProviderKey("TestKey", EntryClass(Data::class))
    @LifeTime(value = 1L, unit = TimeUnit.MINUTES)
    @Expirable
    @UseIfExpired
    suspend fun getData(dataProvider: suspend () -> Data): Data
}