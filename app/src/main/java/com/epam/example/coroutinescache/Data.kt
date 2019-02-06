package com.epam.example.coroutinescache

data class Data (
        val userId: String,
        val id: String,
        val title: String,
        val completed: Boolean
) {
    override fun toString(): String {
        return "UserId: $userId, id: $id, title: $title, completed: $completed"
    }
}