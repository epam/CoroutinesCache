package com.epam.coroutinecache.internal

import com.epam.coroutinecache.api.CacheParams
import com.epam.coroutinecache.core.Persistence
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import org.koin.core.parameter.parametersOf
import org.koin.standalone.KoinComponent
import org.koin.standalone.get
import org.koin.standalone.inject
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

class ProxyProvider(
        private val cacheParams: CacheParams,
        private val scope: CoroutineScope
) : InvocationHandler, KoinComponent {

    private val diskCache: Persistence = get { parametersOf(cacheParams.directory, cacheParams.mapper) }

    private val proxyTranslator: ProxyTranslator by inject()

    private val processorProvider: ProcessorProvider = ProcessorProviderImpl(cacheParams, scope)

    override fun invoke(proxy: Any?, method: Method?, objectMethods: Array<out Any>?): Any? = scope.async {
        return@async processorProvider.process<Any>(proxyTranslator.processMethod(method, objectMethods))
    }
}