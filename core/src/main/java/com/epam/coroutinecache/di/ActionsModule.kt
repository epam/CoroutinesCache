package com.epam.coroutinecache.di

import com.epam.coroutinecache.core.actions.DeleteExpirableRecordsAction
import com.epam.coroutinecache.core.actions.DeleteExpiredRecordsAction
import com.epam.coroutinecache.core.actions.DeleteRecordAction
import com.epam.coroutinecache.core.actions.GetRecordAction
import com.epam.coroutinecache.core.actions.SaveRecordAction
import kotlinx.coroutines.CoroutineScope
import org.koin.dsl.module.module

/**
 * Koin module that contains all actions with cache
 */
val actionsModule = module(override = true) {

    single(name = "DeleteExpiredRecordAction") {
        DeleteExpiredRecordsAction(get(name = "DiskCache"), get(name = "MemoryCache"), get(name = "RecordExpiredChecker"))
    }

    single(name = "DeleteExpirableRecordsAction") { (maxMgPersistenceCache: Int, scope: CoroutineScope) ->
        DeleteExpirableRecordsAction(maxMgPersistenceCache, get(name = "DiskCache"), scope)
    }

    factory(name = "DeleteRecordAction") { (scope: CoroutineScope) ->
        DeleteRecordAction(get(name = "DiskCache"), get(name = "MemoryCache"), scope)
    }

    factory(name = "GetRecordAction") { (scope: CoroutineScope) ->
        GetRecordAction(get(name = "DeleteExpiredRecordAction"), get(name = "RecordExpiredChecker"),
                get(name = "DiskCache"), get(name = "MemoryCache"), scope)
    }

    factory(name = "SaveAction") { (maxMbCacheSize: Int, scope: CoroutineScope) ->
        SaveRecordAction(get(name = "DiskCache"), get(name = "MemoryCache"), maxMbCacheSize, scope)
    }
}