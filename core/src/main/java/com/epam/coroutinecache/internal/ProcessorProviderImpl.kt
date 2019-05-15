package com.epam.coroutinecache.internal

import com.epam.coroutinecache.api.CacheParams
import com.epam.coroutinecache.core.Source
import com.epam.coroutinecache.core.actions.DeleteRecordAction
import com.epam.coroutinecache.core.actions.GetRecordAction
import com.epam.coroutinecache.core.actions.SaveRecordAction
import com.epam.coroutinecache.utils.CacheLog
import kotlinx.coroutines.CoroutineScope
import org.koin.core.parameter.parametersOf
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import kotlin.reflect.full.callSuspend

class ProcessorProviderImpl(
        private val cacheParams: CacheParams,
        private val scope: CoroutineScope
) : ProcessorProvider, KoinComponent {

    private val saveRecordAction: SaveRecordAction by inject { parametersOf(cacheParams.maxPersistenceCacheMB, scope) }

    private val deleteRecordAction: DeleteRecordAction by inject { parametersOf(scope) }

    private val getRecordAction: GetRecordAction by inject { parametersOf(scope) }

    override suspend fun <T> process(cacheObjectParams: CacheObjectParams?): T? {
        if (cacheObjectParams == null) return null
        val record = getRecordAction.getRecord<T>(cacheObjectParams.key, cacheObjectParams.entryType!!, cacheObjectParams.useIfExpired)
        return if (record == null) {
            deleteRecordAction.deleteByKey(cacheObjectParams.key)
            val data = cacheObjectParams.loaderFun?.callSuspend() as T?
            saveRecordAction.save(cacheObjectParams.key, data, cacheObjectParams.entryType!!, cacheObjectParams.timeUnit.toMillis(cacheObjectParams.lifeTime), cacheObjectParams.isExpirable).await()
            CacheLog.logMessage("Got data from source: ${Source.CLOUD}")
            data
        } else {
            CacheLog.logMessage("Got data from source: ${record.getSource()}")
            record.getData()
        }
    }

    override suspend fun deleteAll() {
        deleteRecordAction.deleteAll()
    }

}