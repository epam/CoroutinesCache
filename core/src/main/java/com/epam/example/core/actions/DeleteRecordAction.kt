package com.epam.example.core.actions

import com.epam.example.core.Memory
import com.epam.example.core.Persistence
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async

/**
 * Action to delete record from cache
 */
class DeleteRecordAction(
        private val diskCache: Persistence,
        private val memory: Memory,
        private val scope: CoroutineScope
) {

    /**
     * Async function that deletes record from cache by key
     *
     * @param key - String, key of the record
     */
    suspend fun deleteByKey(key: String) = scope.async {
        diskCache.deleteByKey(key)
        memory.deleteByKey(key)
    }.await()

    /**
     * Async function that deletes all record from cache
     */
    suspend fun deleteAll() = scope.async {
        diskCache.deleteAll()
        memory.deleteAll()
    }.await()
}