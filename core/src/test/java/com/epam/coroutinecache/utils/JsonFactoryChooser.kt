package com.epam.coroutinecache.utils

enum class JsonFactoryChooser(number: Int) {
    MOSHI(0),
    JACKSON(1),
    GSON(2)
}