package com.epam.example.annotations

/**
 * Annotation class using to set caching function expirable value to true. 
 * It means that function's result could be deleted from persistence if persistence reached its memory limit.
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FUNCTION)
annotation class Expirable