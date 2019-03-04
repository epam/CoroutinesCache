package com.epam.example.core

/**
 * Enum that shows where data came from
 */
enum class Source(type: String) {
    MEMORY("memory"),
    PERSISTENCE("persistence"),
    CLOUD("cloud")
}