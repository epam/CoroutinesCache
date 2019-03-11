package com.epam.example.coroutinescache

import com.epam.coroutinecache.api.CacheParams
import com.epam.coroutinecache.api.CoroutinesCache
import com.epam.coroutinecache.mappers.GsonMapper
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class Repository (
        private val cacheDirectory: File
) {

    private val coroutinesCache: CoroutinesCache = CoroutinesCache(CacheParams(10, GsonMapper(), cacheDirectory))

    private val restApi: RestApi = Retrofit.Builder ()
            .baseUrl("https://jsonplaceholder.typicode.com")
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(RestApi::class.java)

    private val cacheProviders: CacheProviders = coroutinesCache.using(CacheProviders::class.java)

    fun getData(): Deferred<Data> = cacheProviders.getData(restApi.getData())
}