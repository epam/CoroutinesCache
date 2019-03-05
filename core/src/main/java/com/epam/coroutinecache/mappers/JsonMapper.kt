package com.epam.coroutinecache.mappers

import java.io.File
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

interface JsonMapper {

    /**
     * This method serializes the specified object, including those of generic types, into its
     * equivalent Json representation. This method must be used if the specified object is a generic
     * type. For non-generic objects, use [.toJson] instead.
     * @param src the object for which JSON representation is to be created
     * @param typeOfSrc The specific genericized type of src.
     * @return Json representation of `src`
     */
    fun toJson(src: Any, typeOfSrc: Type): String

    /**
     * This method deserializes the specified Json into an object of the specified type. This method
     * is useful if the specified object is a generic type. For non-generic objects, use
     * [.fromJson] instead. If you have the Json in a [File] instead of
     * a String, use [.fromJson] instead.
     */
    @Throws(RuntimeException::class)
    fun <T> fromJson(json: String, type: Type): T?

    /**
     * This method deserializes the Json read from the specified reader into an object of the
     * specified type. This method is useful if the specified object is a generic type. For
     * non-generic objects, use [.fromJson] instead. If you have the Json in a
     * String form instead of a [File], use [.fromJson] instead.
     * @param <T> the type of the desired object
     * @param file the file producing Json from which the object is to be deserialized
     * @param typeOfT The specific genericized type of src.
     * @return an object of type T from the json.
    </T> */
    @Throws(RuntimeException::class)
    fun <T> fromJson(file: File, typeOfT: Type): T?

    /**
     * Returns a new parameterized type, applying `typeArguments` to `rawType`.
     */
    fun newParameterizedType(rawType: Type, vararg typeArguments: Type): ParameterizedType
}