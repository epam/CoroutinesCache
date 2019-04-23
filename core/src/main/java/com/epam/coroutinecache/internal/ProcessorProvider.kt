package com.epam.coroutinecache.internal

interface ProcessorProvider {

    /**
     * Provide the data from RxCache
     *
     * @param <T> the associated data
     * @return an observable based on the [ConfigProvider] specs.
    </T> */
    suspend fun <T: Any> process(cacheObjectParams: CacheObjectParams?): T?

    /**
     * Destroy the entire cache
     */
    suspend fun deleteAll()

}