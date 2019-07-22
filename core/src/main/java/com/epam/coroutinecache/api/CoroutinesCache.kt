package com.epam.coroutinecache.api

import com.epam.coroutinecache.di.actionsModule
import com.epam.coroutinecache.di.cacheModule
import com.epam.coroutinecache.internal.ProxyProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import org.koin.core.context.loadKoinModules
import java.lang.reflect.Proxy

/**
 * Entry point of CoroutinesCache. In initialization loads koin modules to apply Dependency Injection.
 *
 * @param cacheParams - CacheParams. {@see CacheParams}
 * @param scope - CoroutinesScope. Scope of the threads, where coroutines will be run
 */
class CoroutinesCache(
        private val cacheParams: CacheParams,
        private val scope: CoroutineScope = GlobalScope
) {

    private lateinit var proxyProvider: ProxyProvider

    init {
        loadKoinModules(listOf(cacheModule, actionsModule))
    }

    /**
     * Function that receive interface as param and create proxy on it.
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> using(clazz: Class<*>): T {
        proxyProvider = ProxyProvider(cacheParams, scope)

        return Proxy.newProxyInstance(clazz.classLoader, arrayOf(clazz), proxyProvider) as T
    }
}