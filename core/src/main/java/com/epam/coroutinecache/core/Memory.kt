package com.epam.coroutinecache.core

interface Memory {

    /**
     * Get the record associated with its particular key
     *
     * @param key The key associated with the Record to be retrieved from memory
     * @see Record
     */
    fun <T: Any> getRecord(key: String): Record<T>?

    /**
     * Save the data supplied based on a certain mechanism which provides storage somehow
     *
     * @param key The key associated with the record to be saved
     * @param record The record to be saved
     */
    fun <T: Any> saveRecord(key: String, record: Record<T>)

    /**
     * Retrieve the keys from all records saved
     */
    fun keySet(): Set<String>

    /**
     * Delete the data associated with its particular key
     *
     * @param key The key associated with the object to be deleted from persistence
     */
    fun deleteByKey(key: String)


    /**
     * Clear all cached data
     */
    fun deleteAll()
}