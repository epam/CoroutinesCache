package com.epam.coroutinecache.di

import com.epam.coroutinecache.core.actions.DeleteExpirableRecordsAction
import com.epam.coroutinecache.core.actions.DeleteExpiredRecordsAction
import com.epam.coroutinecache.core.actions.DeleteRecordAction
import com.epam.coroutinecache.core.actions.GetRecordAction
import com.epam.coroutinecache.core.actions.SaveRecordAction
import kotlinx.coroutines.CoroutineScope
import org.koin.core.qualifier.StringQualifier
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Koin module that contains all actions with cache
 */
val actionsModule = module(override = true) {

    single(qualifier = StringQualifier("DeleteExpiredRecordAction")) {
        DeleteExpiredRecordsAction(
                get(qualifier = StringQualifier("DiskCache")),
                get(qualifier = StringQualifier("MemoryCache")),
                get(qualifier = StringQualifier("RecordExpiredChecker")))
    } bind DeleteExpiredRecordsAction::class

    single(qualifier = StringQualifier("DeleteExpirableRecordsAction")) { (maxMgPersistenceCache: Int, scope: CoroutineScope) ->
        DeleteExpirableRecordsAction(maxMgPersistenceCache,
                get(qualifier = StringQualifier("DiskCache")),
                scope)
    } bind DeleteExpirableRecordsAction::class

    factory(qualifier = StringQualifier("DeleteRecordAction")) { (scope: CoroutineScope) ->
        DeleteRecordAction(
                get(qualifier = StringQualifier("DiskCache")),
                get(qualifier = StringQualifier("MemoryCache")), scope)
    } bind DeleteRecordAction::class

    factory(qualifier = StringQualifier("GetRecordAction")) { (scope: CoroutineScope) ->
        GetRecordAction(
                get(qualifier = StringQualifier("DeleteExpiredRecordAction")),
                get(qualifier = StringQualifier("RecordExpiredChecker")),
                get(qualifier = StringQualifier("DiskCache")),
                get(qualifier = StringQualifier("MemoryCache")), scope)
    } bind GetRecordAction::class

    factory(qualifier = StringQualifier("SaveAction")) { (maxMbCacheSize: Int, scope: CoroutineScope) ->
        SaveRecordAction(
                get(qualifier = StringQualifier("DiskCache")),
                get(qualifier = StringQualifier("MemoryCache")),
                maxMbCacheSize,
                scope)
    } bind SaveRecordAction::class
}