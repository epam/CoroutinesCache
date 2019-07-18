package com.epam.coroutinecache.mappers

import com.squareup.moshi.JsonReader
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import okio.Okio
import java.io.File
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class MoshiMapper(
        private val moshi: Moshi = Moshi.Builder().build()
) : JsonMapper {

    override fun toJson(src: Any, typeOfSrc: Type): String {
        val jsonAdapter = moshi.adapter<Any>(typeOfSrc)
        return jsonAdapter.toJson(src)
    }

    override fun <T> fromJson(json: String, type: Type): T? {
        val jsonAdapter = moshi.adapter<T>(type)
        return jsonAdapter.fromJson(json)
    }

    override fun <T> fromJson(file: File, typeOfT: Type): T? {
        val bufferedSource = Okio.buffer(Okio.source(file))
        val jsonAdapter = moshi.adapter<T>(typeOfT)
        return jsonAdapter.fromJson(JsonReader.of(bufferedSource))
    }

    @Suppress("SpreadOperator")
    override fun newParameterizedType(rawType: Type, vararg typeArguments: Type): ParameterizedType =
            Types.newParameterizedType(rawType, *typeArguments)
}