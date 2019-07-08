package com.epam.example.coroutinescache

import com.epam.coroutinecache.api.CacheParams
import com.epam.coroutinecache.api.CoroutinesCache
import com.epam.coroutinecache.mappers.GsonMapper
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class Repository(
        cacheDirectory: File
) {

    private val coroutinesCache: CoroutinesCache = CoroutinesCache(CacheParams(MAX_CACHE_SIZE_MB, GsonMapper(), cacheDirectory))

    private val restApi: RestApi = Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(RestApi::class.java)

    private val cacheProviders: CacheProviders = coroutinesCache.using(CacheProviders::class.java)

    suspend fun getData(): Data = cacheProviders.getData(restApi::getData)

    companion object {
        private const val MAX_CACHE_SIZE_MB = 10
    }
}