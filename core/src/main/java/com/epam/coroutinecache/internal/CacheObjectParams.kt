package com.epam.coroutinecache.internal

import java.util.concurrent.TimeUnit
import kotlin.reflect.KCallable

data class CacheObjectParams (
        var key: String = "",
        var lifeTime: Long = 0L,
        var timeUnit: TimeUnit = TimeUnit.MILLISECONDS,
        var isExpirable: Boolean = false,
        var useIfExpired: Boolean = false,
        var loaderFun: KCallable<*>? = null
)