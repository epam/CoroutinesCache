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
    private var dataClassName: String? = null
    private var dataCollectionClassName: String? = null
    private var dataKeyMapClassName: String? = null

    @Transient
    var sizeOnMb: Float = 0.0f

    init {
        this.timeAtWhichWasPersisted = System.currentTimeMillis()

        when (data) {
            is Collection<*> -> {
                dataKeyMapClassName = null
                val list = data as List<*>
                if (list.isNotEmpty()) {
                    dataCollectionClassName = List::class.java.name
                    dataClassName = list[0]!!::class.java.name
                } else {
                    dataClassName = null
                    dataCollectionClassName = null
                }
            }
            is Array<*> -> {
                dataKeyMapClassName = null
                val array = data as Array<*>
                if (array.isNotEmpty()) {
                    dataClassName = array[0]!!.javaClass.name
                    dataCollectionClassName = data.javaClass.name
                } else {
                    dataClassName = null
                    dataCollectionClassName = null
                }
            }
            is Map<*, *> -> {
                val map = data as Map<*, *>
                if (map.isNotEmpty()) {
                    dataCollectionClassName = Map::class.java.name

                    val iterator = map.entries.iterator()

                    val firstEntry = iterator.next()
                    var valueClass: Class<*>? = firstEntry.value!!::class.java
                    var keyClass: Class<*>? = firstEntry.key!!::class.java

                    //makes sure, that all the keys and values are of the same type. E.g. values are different for multi-level map.
                    while (iterator.hasNext() || valueClass == null && keyClass == null) {
                        val next = iterator.next()

                        if (keyClass != null && keyClass != next.key!!::class.java) keyClass = null
                        if (valueClass != null && valueClass != next.value!!::class.java) valueClass = null
                    }

                    dataClassName = valueClass?.name
                    dataKeyMapClassName = keyClass?.name
                } else {
                    dataClassName = null
                    dataCollectionClassName = null
                    dataKeyMapClassName = null
                }
            }
            else -> {
                dataKeyMapClassName = null
                dataClassName = data?.getClassName()
                dataCollectionClassName = null
            }
        }
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

    fun getDataClassName(): String? = dataClassName

    fun getDataCollectionClassName(): String? = dataCollectionClassName

    fun getDataKeyMapClassName(): String? = dataKeyMapClassName

    fun getSource(): Source = source

    fun isExpirable(): Boolean = expirable
}