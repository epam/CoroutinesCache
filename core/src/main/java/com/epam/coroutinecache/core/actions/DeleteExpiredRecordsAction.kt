package com.epam.coroutinecache.core.actions

import com.epam.coroutinecache.core.Memory
import com.epam.coroutinecache.core.Persistence
import com.epam.coroutinecache.core.Record
import com.epam.coroutinecache.internal.RecordExpiredChecker

/**
 * Class that used to delete all expired records.
 */
class DeleteExpiredRecordsAction(
        private val diskCache: Persistence,
        private val memory: Memory,
        private val recordExpiredChecker: RecordExpiredChecker
) {

    /**
     *
     */
    fun deleteExpiredRecords() {
        val diskCacheKeys = diskCache.allKeys()

        diskCacheKeys.forEach {
            val record = diskCache.getRecord<Any>(it, Any::class.java)

            if (record != null && recordExpiredChecker.hasRecordExpired(record)) {
                diskCache.deleteByKey(it)
            }
        }

        val memoryCacheKeys = memory.keySet()

        memoryCacheKeys.forEach {
            val record = memory.getRecord<Any>(it)

            if (record != null && recordExpiredChecker.hasRecordExpired(record)) {
                memory.deleteByKey(it)
            }
        }
    }

    /**
     * Fun to check that record is expired and if it is, delete it
     *
     * @param key - string, that is using to delete record from memory and persistence
     * @param record - Record, which is checked if it is expired
     */
    fun deleteExpiredRecord(key: String, record: Record<*>) {
        if (recordExpiredChecker.hasRecordExpired(record)) {
            diskCache.deleteByKey(key)
            memory.deleteByKey(key)
        }
    }
}