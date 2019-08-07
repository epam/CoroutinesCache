package com.epam.coroutinecache.internal

import com.epam.coroutinecache.annotations.Expirable
import com.epam.coroutinecache.annotations.LifeTime
import com.epam.coroutinecache.annotations.ProviderKey
import com.epam.coroutinecache.annotations.UseIfExpired
import com.epam.coroutinecache.api.ParameterizedDataProvider
import com.epam.coroutinecache.utils.NoParamsDataProvider
import com.epam.coroutinecache.utils.Types
import java.lang.reflect.Method
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit
import kotlin.reflect.KCallable

/**
 * Class that retrieves all object params from annotations and function that
 * returns data that should be stored
 */
class ProxyTranslator {

    @Suppress("ReturnCount")
    fun processMethod(method: Method?, methodArgs: Array<out Any>?): CacheObjectParams? {
        if (method == null) {
            return null
        }
        val cacheObjectParams = CacheObjectParams()
        val lifeTime = getMethodLifeTime(method)
        if (lifeTime != null) {
            cacheObjectParams.lifeTime = lifeTime.first
            cacheObjectParams.timeUnit = lifeTime.second
        }
        cacheObjectParams.isExpirable = isMethodExpirable(method)
        cacheObjectParams.useIfExpired = useMethodIfExpired(method)
        cacheObjectParams.dataProvider = getDataSuspend(method, methodArgs)
        val baseKey = getMethodKey(method)
        cacheObjectParams.key = cacheObjectParams.dataProvider?.parameterizeKey(baseKey) ?: baseKey
        cacheObjectParams.entryType = getMethodType(method)

        return cacheObjectParams
    }

    private fun getMethodLifeTime(method: Method): Pair<Long, TimeUnit>? {
        val lifeTimeAnnotation = method.getAnnotation(LifeTime::class.java) ?: return null
        return Pair(lifeTimeAnnotation.value, lifeTimeAnnotation.unit)
    }

    private fun isMethodExpirable(method: Method): Boolean {
        val annotation = method.getAnnotation(Expirable::class.java)
        return annotation != null
    }

    private fun useMethodIfExpired(method: Method): Boolean {
        val annotation = method.getAnnotation(UseIfExpired::class.java)
        return annotation != null
    }

    private fun getMethodKey(method: Method): String {
        val annotation = method.getAnnotation(ProviderKey::class.java)
                ?: return method.name + method.declaringClass + method.returnType
        return annotation.key
    }

    private fun getMethodType(method: Method): Type {
        val providerAnnotation = method.getAnnotation(ProviderKey::class.java)
        return Types.obtainTypeFromAnnotation(providerAnnotation.entryClass)
    }

    private fun getDataSuspend(method: Method, methodArgs: Array<out Any>?): ParameterizedDataProvider<*> {
        val dataProvider = getObjectFromMethodParam(method, ParameterizedDataProvider::class.java, methodArgs)
        if (dataProvider != null) {
            return dataProvider
        }
        val callable = getObjectFromMethodParam(method, KCallable::class.java, methodArgs)
        if (callable == null || !callable.isSuspend)
            throw IllegalStateException("${method.name} requires a ParameterizedDataProvider implementation or parameterless suspend function")
        return NoParamsDataProvider(callable)
    }

    private fun <T> getObjectFromMethodParam(method: Method, expectedClass: Class<T>, methodArgs: Array<out Any>?): T? {
        if (methodArgs == null) return null
        var countSameObjectsType = 0
        var expectedObject: T? = null

        for (objectParam in methodArgs) {
            if (expectedClass.isAssignableFrom(objectParam::class.java)) {
                expectedObject = objectParam as T
                ++countSameObjectsType
            }
        }

        if (countSameObjectsType > 1) {
            throw IllegalArgumentException("${method.name} requires just one instance of type ${expectedClass.simpleName}")
        }

        return expectedObject
    }
}