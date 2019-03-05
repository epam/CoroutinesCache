package com.epam.coroutinecache.annotations

/**
 * Annotation that describes key, which will be used to store function result
 *
 * @param key - data's key in cache
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FUNCTION)
annotation class ProviderKey(val key: String)