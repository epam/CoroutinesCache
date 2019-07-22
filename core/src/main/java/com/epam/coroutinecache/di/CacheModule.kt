package com.epam.coroutinecache.di

import com.epam.coroutinecache.core.DiskCache
import com.epam.coroutinecache.core.Memory
import com.epam.coroutinecache.core.MemoryCache
import com.epam.coroutinecache.core.Persistence
import com.epam.coroutinecache.internal.ProxyTranslator
import com.epam.coroutinecache.internal.RecordExpiredChecker
import com.epam.coroutinecache.mappers.JsonMapper
import org.koin.core.qualifier.StringQualifier
import org.koin.dsl.bind
import org.koin.dsl.module
import java.io.File

/**
 * Koin modules that contains all instances regarding to cache
 */
val cacheModule = module(override = true) {

    single(qualifier = StringQualifier("DiskCache"), override = true) { (cacheDirectory: File, mapper: JsonMapper) ->
        DiskCache(cacheDirectory, mapper)
    } bind Persistence::class

    single(qualifier = StringQualifier("MemoryCache")) {
        MemoryCache()
    } bind Memory::class

    factory(qualifier = StringQualifier("RecordExpiredChecker")) {
        RecordExpiredChecker()
    }

    single(qualifier = StringQualifier("ProxyTranslator")) {
        ProxyTranslator()
    } bind ProxyTranslator::class
}