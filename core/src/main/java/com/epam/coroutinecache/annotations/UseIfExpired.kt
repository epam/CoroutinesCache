package com.epam.coroutinecache.annotations

/**
 * If this annotation is applying to function, user will get data from cache even lifetime is exceeded
 * When data is received, they will be deleted from cache.
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class UseIfExpired