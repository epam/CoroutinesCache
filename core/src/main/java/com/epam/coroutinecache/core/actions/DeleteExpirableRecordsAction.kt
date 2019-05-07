package com.epam.coroutinecache.core.actions

import com.epam.coroutinecache.core.Persistence
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async

/**
 * Action that works with persistence and remove expirable records if
 * persistence reached memory limit.
 */
class DeleteExpirableRecordsAction(
        private val maxMgPersistenceCache: Int,
        private val diskCache: Persistence,
        private val scope: CoroutineScope
) {

    /**
     * Async function checks if persistence reached memory limit and remove expirable
     * records until memory is not enough to store new records.
     */
    fun deleteExpirableRecords() = scope.async {
        val storedMB = diskCache.storedMB()
        if (!reachedPercentageMemoryToStart(storedMB)) {
            return@async
        }
        val keys = diskCache.allKeys()

        var releasedMB = 0.0f
        run loop@{
            keys.forEach {
                if (reachedPercentageMemoryToStop(storedMB, releasedMB)) {
                    return@loop
                }
                val record = diskCache.getRecord<Any>(it, Any::class.java)
                if (record == null || !record.isExpirable()) return@forEach
                diskCache.deleteByKey(it)
                releasedMB += record.sizeOnMb
            }
        }
    }

    private fun reachedPercentageMemoryToStop(storedMBWhenStarted: Long, releasedMBSoFar: Float): Boolean {
        val currentStoredMB = storedMBWhenStarted - releasedMBSoFar
        val requiredStoredMBToStop = maxMgPersistenceCache * PERCENTAGE_MEMORY_STORED_TO_STOP
        return currentStoredMB <= requiredStoredMBToStop
    }

    private fun reachedPercentageMemoryToStart(storedMB: Long): Boolean {
        val requiredStoredMBToStart = maxMgPersistenceCache * PERCENTAGE_MEMORY_STORED_TO_START
        return storedMB >= requiredStoredMBToStart
    }

    companion object {
        private const val PERCENTAGE_MEMORY_STORED_TO_START = 0.95f
        private const val PERCENTAGE_MEMORY_STORED_TO_STOP = 0.7f
    }

}