package com.epam.example.di

import com.epam.example.api.CacheParams
import com.epam.example.core.DiskCache
import com.epam.example.core.Memory
import com.epam.example.core.MemoryCache
import com.epam.example.core.Persistence
import com.epam.example.internal.ProcessorProvider
import com.epam.example.internal.ProcessorProviderImpl
import com.epam.example.internal.ProxyTranslator
import com.epam.example.internal.RecordExpiredChecker
import com.epam.example.mappers.JsonMapper
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

