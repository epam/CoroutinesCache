package com.epam.coroutinecache.core


/**
 * Provides the persistence layer for the cache A default implementation which store the objects in
 * disk is supplied:
 *
 * @see DiskCache
 */
interface Persistence {

    /**
     * Save the data supplied based on a certain mechanism which provides persistence somehow
     *
     * @param key The key associated with the record to be persisted
     * @param record The record to be persisted
     */
    fun <T> saveRecord(key: String, record: Record<T>)

    /**
     * Delete the data associated with its particular key
     *
     * @param key The key associated with the object to be deleted from persistence
     */
    fun deleteByKey(key: String)

    /**
     * Delete all the data
     */
    fun deleteAll()

    /**
     * Retrieve the keys from all records persisted
     */
    fun allKeys(): List<String>

    /**
     * Retrieve accumulated memory records in megabytes
     */
    fun storedMB(): Long

    /**
     * Get the record associated with its particular key
     *
     * @param key The key associated with the Record to be retrieved from persistence
     * @see Record
     */
    fun <T> getRecord(key: String): Record<T>?
}