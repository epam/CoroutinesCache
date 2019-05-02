package com.epam.coroutinecache.internal

interface ProcessorProvider {

    /**
     * Provide the data from the cache
     *
     * @param cacheObjectParams params for requesting cache object
     * @return <T> the associated data
    */
    suspend fun <T> process(cacheObjectParams: CacheObjectParams?): T?

    /**
     * Destroy the entire cache
     */
    suspend fun deleteAll()

}