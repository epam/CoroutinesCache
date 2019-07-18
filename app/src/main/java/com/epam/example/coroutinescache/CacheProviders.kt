package com.epam.example.coroutinescache

import com.epam.coroutinecache.annotations.ProviderKey
import com.epam.coroutinecache.annotations.LifeTime
import com.epam.coroutinecache.annotations.Expirable
import com.epam.coroutinecache.annotations.EntryClass
import com.epam.coroutinecache.annotations.UseIfExpired
import com.epam.coroutinecache.api.DataProvider
import java.util.concurrent.TimeUnit

interface CacheProviders {

    @ProviderKey("TestKey", EntryClass(Data::class))
    @LifeTime(value = 1L, unit = TimeUnit.MINUTES)
    @Expirable
    @UseIfExpired
    suspend fun getData(provider: DataProvider<Data>): Data
}