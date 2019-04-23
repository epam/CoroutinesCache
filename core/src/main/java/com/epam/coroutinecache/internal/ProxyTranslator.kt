package com.epam.coroutinecache.internal

import com.epam.coroutinecache.annotations.*
import com.epam.coroutinecache.utils.Types
import kotlinx.coroutines.Deferred
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import java.lang.reflect.Method
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit

/**
 * Class that retrieves all object params from annotations and function that
 * returns data that should be stored
 */
class ProxyTranslator {

    private val cacheObjectParamsMap: MutableMap<Method, CacheObjectParams> = HashMap()

    fun processMethod(method: Method?, objectMethods: Array<out Any>?): CacheObjectParams? {
        if (method == null) {
            return null
        }
        if (cacheObjectParamsMap[method] != null) {
            return cacheObjectParamsMap[method]!!
        }

        val cacheObjectParams = CacheObjectParams()
        val lifeTime = getMethodLifeTime(method)
        if (lifeTime != null) {
            cacheObjectParams.lifeTime = lifeTime.first
            cacheObjectParams.timeUnit = lifeTime.second
        }
        cacheObjectParams.isExpirable = isMethodExpirable(method)
        cacheObjectParams.useIfExpired = useMethodIfExpired(method)
        cacheObjectParams.key = getMethodKey(method)
        cacheObjectParams.loaderFun = getDataDeferred(method, objectMethods)
        cacheObjectParams.entryType = getMethodType(method)

        cacheObjectParamsMap[method] = cacheObjectParams

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
        val annotation = method.getAnnotation(ProviderKey::class.java) ?: return method.name + method.declaringClass + method.returnType
        return annotation.key
    }

    private fun getMethodType(method: Method): Type {
        val providerAnnotation = method.getAnnotation(ProviderKey::class.java)
        return Types.obtainTypeFromAnnotation(providerAnnotation.entryClass)
    }

    private fun getDataDeferred(method: Method, objectMethods: Array<out Any>?): Deferred<*> {
        val deferred: Deferred<*> = getObjectFromMethodParam(method, Deferred::class.java, objectMethods) ?:
                throw IllegalStateException("${method.name} requires an deferred instance")
        return deferred
    }

    private fun <T> getObjectFromMethodParam(method: Method, expectedClass: Class<T>, objectMethods: Array<out Any>?): T? {
        if (objectMethods == null) return null
        var countSameObjectsType = 0
        var expectedObject: T? = null

        for (objectParam in objectMethods) {
            if (expectedClass.isAssignableFrom(objectParam!!::class.java)) {
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