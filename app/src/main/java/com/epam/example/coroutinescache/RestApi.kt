package com.epam.example.coroutinescache

import retrofit2.http.GET
import retrofit2.http.Query

interface RestApi {

    @GET("todos/1")
    suspend fun getData(): Data

    @GET("todos/1")
    suspend fun getParameterizedData(@Query("search") search: String): Data
}