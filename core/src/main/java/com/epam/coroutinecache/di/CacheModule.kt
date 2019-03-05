package com.epam.coroutinecache.di

import com.epam.coroutinecache.core.DiskCache
import com.epam.coroutinecache.core.Memory
import com.epam.coroutinecache.core.MemoryCache
import com.epam.coroutinecache.core.Persistence
import com.epam.coroutinecache.internal.ProxyTranslator
import com.epam.coroutinecache.internal.RecordExpiredChecker
import com.epam.coroutinecache.mappers.JsonMapper
import org.koin.dsl.module.module
import java.io.File

/**
 * Koin modules that contains all instances regarding to cache
 */
val cacheModule = module (override = true) {
    single (name = "DiskCache", override = true) { (cacheDirectory: File, mapper: JsonMapper) -> DiskCache(cacheDirectory, mapper) } bind Persistence::class
    single (name = "MemoryCache") { MemoryCache() } bind Memory::class
    factory (name = "RecordExpiredChecker") { RecordExpiredChecker() }
    single (name = "ProxyTranslator") { ProxyTranslator() }
}

