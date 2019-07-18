package com.epam.coroutinecache.internal

import com.epam.coroutinecache.api.DataProvider
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit

data class CacheObjectParams(
        var key: String = "",
        var lifeTime: Long = 0L,
        var timeUnit: TimeUnit = TimeUnit.MILLISECONDS,
        var isExpirable: Boolean = false,
        var useIfExpired: Boolean = false,
        var dataProvider: DataProvider<*>? = null,
        var entryType: Type? = null
)