package com.epam.coroutinecache.core

import com.epam.coroutinecache.utils.getClassName

/**
 * Class that will be stored in the cache. Contains data and all information about record in the cache
 *
 * @param data - Any data that need to be stored.
 * @param expirable - Boolean that defines possibility of deleting record from cache in low memory case
 * @param lifeTimeMillis - Long. Record life time in millis
 * @param source - Source from data was retrieved
 */
class Record<T>(
        private val data: T? = null,
        private var expirable: Boolean = true,
        private var lifeTimeMillis: Long = 0
) {
    private var source: Source = Source.MEMORY
    private var timeAtWhichWasPersisted: Long = 0

    @Transient
    var sizeOnMb: Float = 0.0f

    init {
        this.timeAtWhichWasPersisted = System.currentTimeMillis()
    }

    fun setSource(source: Source) {
        this.source = source
    }

    fun setLifeTime(lifeTime: Long) {
        this.lifeTimeMillis = lifeTime
    }

    fun getData(): T? = data

    fun getLifeTimeMillis(): Long = lifeTimeMillis

    fun getTimeAtWhichWasPersisted(): Long = timeAtWhichWasPersisted

    fun getSource(): Source = source

    fun isExpirable(): Boolean = expirable
}