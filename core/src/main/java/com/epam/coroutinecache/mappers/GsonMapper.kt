package com.epam.coroutinecache.mappers

import com.epam.coroutinecache.utils.Types
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class GsonMapper(
        private val gson: Gson = Gson()
): JsonMapper {

    override fun toJson(src: Any, typeOfSrc: Type): String = gson.toJson(src, typeOfSrc)

    override fun <T> fromJson(json: String, type: Type): T? = gson.fromJson(json, type)

    override fun <T> fromJson(file: File, typeOfT: Type): T? {
        val reader = BufferedReader(FileReader(file.absoluteFile))
        val objectValue: T = gson.fromJson(reader, typeOfT)
        reader.close()
        return objectValue
    }

    override fun newParameterizedType(rawType: Type, vararg typeArguments: Type): ParameterizedType = Types.newParameterizedType(rawType, *typeArguments)
}