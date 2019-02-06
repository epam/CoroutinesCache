package com.epam.example.annotations

import java.util.concurrent.TimeUnit

/**
 * Annotation that describes how long function result should be saved in cache
 *
 * @param value - number that describes lifetime
 * @param unit - TimeUnit of lifetime
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FUNCTION)
annotation class LifeTime (val value: Long, val unit: TimeUnit)