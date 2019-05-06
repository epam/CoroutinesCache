package com.epam.coroutinecache.internal

import com.epam.coroutinecache.annotations.Expirable
import com.epam.coroutinecache.annotations.LifeTime
import com.epam.coroutinecache.annotations.ProviderKey
import com.epam.coroutinecache.annotations.UseIfExpired
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import java.lang.reflect.Method
import java.util.concurrent.TimeUnit
import kotlin.reflect.KCallable

/**
 * Class that retrieves all object params from annotations and function that
 * returns data that should be stored
 */
class ProxyTranslator {

    private val cacheObjectParamsMap: MutableMap<Method, CacheObjectParams> = HashMap()

    fun processMethod(method: Method?, methodArgs: Array<out Any>?): CacheObjectParams? {
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

        cacheObjectParams.loaderFun = getDataSuspend(method, methodArgs)

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

    private fun getDataSuspend(method: Method, methodArgs: Array<out Any>?): KCallable<*> {
        val dataProvider: KCallable<*>? = getObjectFromMethodParam(method, KCallable::class.java, methodArgs)
        if (dataProvider == null || !dataProvider.isSuspend) throw IllegalStateException("${method.name} requires an suspend instance")
        return dataProvider
    }

    private fun <T> getObjectFromMethodParam(method: Method, expectedClass: Class<T>, methodArgs: Array<out Any>?): T? {
        if (methodArgs == null) return null
        var countSameObjectsType = 0
        var expectedObject: T? = null

        for (objectParam in methodArgs) {
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