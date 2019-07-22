package com.epam.coroutinecache.internal

import com.epam.coroutinecache.api.CacheParams
import com.epam.coroutinecache.core.Persistence
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.get
import org.koin.core.inject
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import org.koin.core.parameter.parametersOf
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume

class ProxyProvider(
        private val cacheParams: CacheParams,
        private val scope: CoroutineScope
) : InvocationHandler, KoinComponent {

    private val diskCache: Persistence = get { parametersOf(cacheParams.directory, cacheParams.mapper) }

    private val proxyTranslator: ProxyTranslator by inject()

    private val processorProvider: ProcessorProvider = ProcessorProviderImpl(cacheParams, scope)

    override fun invoke(proxy: Any?, method: Method?, methodArgs: Array<out Any>?): Any? {
        val lastArg = methodArgs?.lastOrNull()
        return if (lastArg is Continuation<*>) {
            @Suppress("UNCHECKED_CAST")
            val cont = lastArg as? Continuation<Any?>
            val otherArgs = methodArgs.take(methodArgs.size - 1).toTypedArray()
            scope.launch {
                val data = processorProvider.process<Any>(proxyTranslator.processMethod(method, otherArgs))
                cont?.resume(data)
            }
            COROUTINE_SUSPENDED
        } else {
            null
        }
    }
}