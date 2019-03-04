package com.epam.example.utils

class MockDataString (
        private val message: String
) {

    constructor(): this("test")

    fun getMessage(): String = message

    override fun equals(other: Any?): Boolean {
        return other!= null && other is MockDataString && other.message == this.message
    }
}