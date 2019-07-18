package com.epam.coroutinecache.utils

@Suppress("ConstantConditionIf")
object CacheLog {

    private const val logsEnabled = false

    fun logMessage(message: String) {
        if (logsEnabled) {
            println(message)
        }
    }

    fun logError(message: String, throwable: Throwable) {
        if (logsEnabled) {
            println("Error: $message and error message: ${throwable.localizedMessage}")
        }
    }
}