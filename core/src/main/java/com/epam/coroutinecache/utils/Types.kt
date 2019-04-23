package com.epam.coroutinecache.utils

import com.epam.coroutinecache.annotations.EntryClass
import java.lang.reflect.*

/**
 * Factory methods for types.
 */
object Types {

    private val EMPTY_TYPE_ARRAY = emptyArray<Type>()

    fun obtainTypeFromAnnotation(annotation: EntryClass): Type {
        return if (annotation.typeParams.isEmpty()) {
            if (annotation.rawType.javaObjectType.isArray) {
                Types.arrayOf(annotation.rawType.javaObjectType)
            } else {
                annotation.rawType.javaObjectType
            }
        } else {
            Types.newParameterizedType(annotation.rawType.javaObjectType, *annotation.typeParams.map { obtainTypeFromAnnotation(it) }.toTypedArray())
        }
    }

    /**
     * Returns a new parameterized type, applying {@code typeArguments} to {@code rawType}.
     */
    fun newParameterizedType(rawType: Type, vararg types: Type): ParameterizedType = ParameterizedTypeImpl(null, rawType, arrayOf(*types))

    /**
     * Returns an array type whose elements are all instances of `componentType`.
     */
    fun arrayOf(componentType: Type): GenericArrayType = GenericArrayTypeImpl(componentType)

    fun canonicalize(type: Type): Type {
        when(type) {
            is Class<*> -> {
                return if (type.isArray) GenericArrayTypeImpl(canonicalize(type.componentType)) else type
            }
            is ParameterizedType -> {
                if (type is ParameterizedTypeImpl) return type
                return ParameterizedTypeImpl(type.ownerType, type.rawType, type.actualTypeArguments)
            }
            is GenericArrayType -> {
                if (type is GenericArrayTypeImpl) return type
                return GenericArrayTypeImpl(type.genericComponentType)
            }
            is WildcardType -> {
                if (type is WildcardTypeImpl) return type
                return WildcardTypeImpl(type.upperBounds, type.lowerBounds)
            }
            else -> {
                return type
            }
        }
    }

    private class ParameterizedTypeImpl(
            private var ownerType: Type?,
            private var rawType: Type,
            private var typeArguments: Array<Type?>
    ): ParameterizedType {

        init {
            if (rawType is Class<*>) {
                val isStaticOrTopLevelClass = Modifier.isStatic((rawType as Class<*>).modifiers) || (rawType as Class<*>).enclosingClass == null
                if (ownerType == null && !isStaticOrTopLevelClass) throw IllegalArgumentException()
            }

            this.ownerType = if (ownerType == null) null else canonicalize(this.ownerType!!)
            this.rawType = canonicalize(this.rawType)
            typeArguments = typeArguments.filter { it != null }.map { canonicalize(it!!) }.toTypedArray()
        }

        override fun getRawType(): Type = rawType

        override fun getOwnerType(): Type? = ownerType

        override fun getActualTypeArguments(): Array<Type?> = typeArguments.clone()
    }

    private class GenericArrayTypeImpl(private var componentType: Type): GenericArrayType {

        init {
            this.componentType = canonicalize(componentType)
        }

        override fun getGenericComponentType(): Type = componentType
    }

    /**
     * The WildcardType interface supports multiple upper bounds and multiple lower bounds. We only
     * support what the Java 6 language needs - at most one bound. If a lower bound is set, the upper
     * bound must be Object.class.
     */
    private class WildcardTypeImpl(upperBounds: Array<Type?>, lowerBounds: Array<Type?>) : WildcardType {

        private var upperBound: Type?
        private var lowerBound: Type?

        init {
            if (upperBounds.size != 1) throw IllegalArgumentException()
            if (lowerBounds.size > 1) throw IllegalArgumentException()

            if (lowerBounds.size == 1) {
                if (lowerBounds[0] == null) throw NullPointerException()
                if (upperBounds[0] !== Any::class.java) throw IllegalArgumentException()
                this.lowerBound = canonicalize(lowerBounds[0]!!)
                this.upperBound = Any::class.java

            } else {
                if (upperBounds[0] == null) throw NullPointerException()
                this.lowerBound = null
                this.upperBound = canonicalize(upperBounds[0]!!)
            }
        }

        override fun getLowerBounds(): Array<Type> = if (lowerBound != null) arrayOf<Type>(lowerBound!!) else EMPTY_TYPE_ARRAY

        override fun getUpperBounds(): Array<Type> = arrayOf<Type>(upperBound!!)
    }
}