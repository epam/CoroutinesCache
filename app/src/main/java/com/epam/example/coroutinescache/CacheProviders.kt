package com.epam.example.coroutinescache

import com.epam.example.annotations.Expirable
import com.epam.example.annotations.LifeTime
import com.epam.example.annotations.ProviderKey
import com.epam.example.annotations.UseIfExpired
import kotlinx.coroutines.Deferred
import java.util.concurrent.TimeUnit

interface CacheProviders {

    @ProviderKey("TestKey")
    @LifeTime(value = 1L, unit = TimeUnit.MINUTES)
    @Expirable
    @UseIfExpired
    fun getData(data: Deferred<Data>): Deferred<Data>
}