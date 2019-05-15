package com.epam.example.coroutinescache

import retrofit2.http.GET

interface RestApi {

    @GET("todos/1")
    suspend fun getData(): Data
}