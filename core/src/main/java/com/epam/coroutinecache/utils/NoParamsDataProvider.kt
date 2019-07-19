package com.epam.coroutinecache.utils

import com.epam.coroutinecache.api.ParameterizedDataProvider
import kotlin.reflect.KCallable
import kotlin.reflect.full.callSuspend

class NoParamsDataProvider<T>(private val callable: KCallable<T>) : ParameterizedDataProvider<T> {

    override suspend fun getData(): T = callable.callSuspend()
}