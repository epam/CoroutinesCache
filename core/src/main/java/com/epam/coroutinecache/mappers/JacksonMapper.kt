package com.epam.example.mappers

import com.epam.example.utils.Types
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.File
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class JacksonMapper(
        private val objectMapper: ObjectMapper = ObjectMapper()
): JsonMapper {

    init {
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    override fun toJson(src: Any, typeOfSrc: Type): String = objectMapper.writeValueAsString(src)

    override fun <T> fromJson(json: String, type: Type): T {
        val typeReference: TypeReference<T> = object: TypeReference<T>() {
            override fun getType(): Type = type
        }
        return objectMapper.readValue(json, typeReference)
    }

    override fun <T> fromJson(file: File, typeOfT: Type): T? {
        val typeReference: TypeReference<T> = object: TypeReference<T>() {
            override fun getType(): Type = typeOfT
        }
        return objectMapper.readValue(file, typeReference)
    }

    override fun newParameterizedType(rawType: Type, vararg typeArguments: Type): ParameterizedType = Types.newParameterizedType(rawType, *typeArguments)
}