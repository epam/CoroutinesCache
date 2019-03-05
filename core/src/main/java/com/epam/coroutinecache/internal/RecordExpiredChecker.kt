package com.epam.coroutinecache.internal

import com.epam.coroutinecache.core.Record

/**
 * Helper class that checks is record expired
 */
class RecordExpiredChecker {

    fun hasRecordExpired(record: Record<*>): Boolean {
        val now = System.currentTimeMillis()

        val lifetime = record.getLifeTimeMillis()
        if (lifetime == 0L) return false

        return now - record.getTimeAtWhichWasPersisted() > lifetime
    }
}