package com.epam.example.coroutinescache

import com.epam.coroutinecache.api.CacheParams
import com.epam.coroutinecache.api.CoroutinesCache
import com.epam.coroutinecache.api.ParameterizedDataProvider
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

    suspend fun getParameterizedData(search: String): Data = cacheProviders.getParametrizedData(DataProviderImpl(search))

    private inner class DataProviderImpl(private val search: String) : ParameterizedDataProvider<Data> {

        override suspend fun getData(): Data = restApi.getParameterizedData(search)

        override fun parameterizeKey(baseKey: String): String = "${baseKey}_$search"
    }

    companion object {
        private const val MAX_CACHE_SIZE_MB = 10
    }
}