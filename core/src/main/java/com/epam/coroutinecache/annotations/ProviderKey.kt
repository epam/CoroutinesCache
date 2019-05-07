package com.epam.coroutinecache.annotations

import kotlin.reflect.KClass

/**
 * Annotation that describes key, which will be used to store function result
 *
 * @param key - data's key in cache
 * @param entryClass - entry's class in cache, [@link com.epam.coroutinecache.annotations.EntryClass]
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class ProviderKey(val key: String, val entryClass: EntryClass)

/**
 * Annotation that describes how to build type for entry class
 *
 * @param rawType - data's raw type, could be Foo:class, for list List::class should be passed
 * @param typeParams - data's type params, for example, for List you should pass entire class, for example Foo::class, or for Map you should pass two classes - first will be used for key and second for value
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class EntryClass(val rawType: KClass<*>, vararg val typeParams: EntryClass = [])