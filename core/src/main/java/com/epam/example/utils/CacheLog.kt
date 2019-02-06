package com.epam.example.utils

object CacheLog {

    private val logsEnabled = false

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