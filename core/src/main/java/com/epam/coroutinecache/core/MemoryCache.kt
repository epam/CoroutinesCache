package com.epam.example.core

import com.epam.example.utils.CacheLog

class MemoryCache : Memory {

    private val recordsMap: MutableMap<in String, Record<*>> = mutableMapOf()

    override fun <T> getRecord(key: String): Record<T>? {
        synchronized(recordsMap) {
            try {
                return if (recordsMap[key] == null) {
                    null
                } else {
                    recordsMap[key] as Record<T>
                }
            } catch (e: Exception) {
                CacheLog.logError("Cannot get record from memory", e)
                return null
            }
        }
    }

    override fun <T> saveRecord(key: String, record: Record<T>) {
        synchronized(recordsMap) {
            recordsMap.put(key, record)
        }
    }

    override fun keySet(): Set<String> = recordsMap.keys as Set<String>

    override fun deleteByKey(key: String) {
        synchronized(recordsMap) {
            recordsMap.remove(key)
        }
    }

    override fun deleteAll() {
        synchronized(recordsMap) {
            recordsMap.clear()
        }
    }
}
