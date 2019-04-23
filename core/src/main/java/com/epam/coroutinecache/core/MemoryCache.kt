package com.epam.coroutinecache.core

import com.epam.coroutinecache.utils.CacheLog
import java.util.concurrent.ConcurrentHashMap

class MemoryCache : Memory {

    private val recordsMap: MutableMap<in String, Record<*>> = ConcurrentHashMap()

    override fun <T: Any> getRecord(key: String): Record<T>? {
        return try {
            if (recordsMap[key] == null) {
                null
            } else {
                recordsMap[key] as Record<T>
            }
        } catch (e: Exception) {
            CacheLog.logError("Cannot get record from memory", e)
            null
        }
    }

    override fun <T: Any> saveRecord(key: String, record: Record<T>) {
        recordsMap[key] = record
    }

    override fun keySet(): Set<String> = recordsMap.keys as Set<String>

    override fun deleteByKey(key: String) {
        recordsMap.remove(key)
    }

    override fun deleteAll() {
        recordsMap.clear()
    }

}
