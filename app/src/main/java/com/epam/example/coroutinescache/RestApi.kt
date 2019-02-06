package com.epam.example.coroutinescache

import kotlinx.coroutines.Deferred
import retrofit2.http.GET

interface RestApi {

    @GET("todos/1")
    fun getData(): Deferred<Data>
}