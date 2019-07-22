package com.epam.coroutinecache.api

/**
 * Implementation of this interface will allow CoroutineCache to get data when they are absent in cache.
 * It also will allow to modify cache key in situation of parameterized calls.
 */
interface ParameterizedDataProvider<T> {

    suspend fun getData(): T

    fun parameterizeKey(baseKey: String): String = baseKey
}