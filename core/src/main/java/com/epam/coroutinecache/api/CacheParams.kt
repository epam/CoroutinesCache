package com.epam.coroutinecache.api

import com.epam.coroutinecache.mappers.JsonMapper
import java.io.File

/**
 * Class contains params for CoroutinesCache. Cache directory should exists and has a write permissions.
 *
 * @param maxPersistenceCacheMB - Int. Max size of cached data in Mb
 * @param mapper - JsonMapper. One of implementation JsonMapper interface that will be used for serialization data
 * @param directory - File. Directory, where cache files will be stored
 */
data class CacheParams(
        val maxPersistenceCacheMB: Int,
        val mapper: JsonMapper,
        val directory: File
) {
    init {
        when {
            !this.directory.exists() -> throw IllegalArgumentException("Cache directory should be non-null")
            !this.directory.canWrite() -> throw IllegalArgumentException("Cache directory is not writable")
        }
    }
}