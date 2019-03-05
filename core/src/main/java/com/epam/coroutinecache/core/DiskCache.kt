package com.epam.coroutinecache.core

import com.epam.coroutinecache.mappers.JsonMapper
import java.io.File
import java.io.FileWriter
import java.lang.Exception
import java.lang.reflect.ParameterizedType

class DiskCache(
        private val cacheDirectory: File,
        private val jsonMapper: JsonMapper
): Persistence {

    override fun <T> saveRecord(key: String, record: Record<T>) = synchronized(this) {
        val safetyKey = safetyKey(key)
        val type = jsonMapper.newParameterizedType(Record::class.java, Object::class.java)
        val serializedJson = jsonMapper.toJson(record, type)

        val resultedFile = File(cacheDirectory, safetyKey)
        val writer = FileWriter(resultedFile, false)
        writer.write(serializedJson)
        writer.flush()
        writer.close()
    }

    override fun deleteByKey(key: String) {
        synchronized(this) {
            val safetyKey = safetyKey(key)
            val resultedFile = File(cacheDirectory, safetyKey)
            resultedFile.delete()
        }
    }

    override fun deleteAll() {
        synchronized(this) {
            val files = cacheDirectory.listFiles()
            files?.forEach {
                it?.delete()
            }
        }
    }

    override fun allKeys(): List<String> {
        synchronized(this) {
            val keys = mutableListOf<String>()
            val files = cacheDirectory.listFiles()
            files?.forEach {
                if (it.isFile) {
                    keys.add(it.name)
                }
            }
            return keys
        }
    }

    override fun storedMB(): Long {
        synchronized(this) {
            var result: Long = 0
            val files = cacheDirectory.listFiles()
            files?.forEach {
                if (it.isFile) {
                    result += it.length()
                }
            }
            return (result.toFloat() / 1024 / 1024).toLong()
        }
    }

    override fun <T> getRecord(key: String): Record<T>? {
        synchronized(this) {
            try {
                val safetyKey = safetyKey(key)
                val resultedFile = File(cacheDirectory, safetyKey)
                val type = jsonMapper.newParameterizedType(Record::class.java, Object::class.java)
                val tempData: Record<T>? = jsonMapper.fromJson(resultedFile, type)

                val dataClassName = if (tempData?.getDataClassName() == null) Object::class.java else Class.forName(tempData.getDataClassName())
                val collectionClassName = if (tempData?.getDataCollectionClassName() == null) Object::class.java else Class.forName(tempData.getDataCollectionClassName())

                val isCollection = Collection::class.java.isAssignableFrom(collectionClassName)
                val isArray = collectionClassName.isArray
                val isMap = Map::class.java.isAssignableFrom(collectionClassName)
                val typeRecord: ParameterizedType

                when {
                    isCollection -> {
                        val typeCollection = jsonMapper.newParameterizedType(collectionClassName, dataClassName)
                        typeRecord = jsonMapper.newParameterizedType(Record::class.java, typeCollection)
                    }
                    isArray -> {
                        typeRecord = jsonMapper.newParameterizedType(Record::class.java, collectionClassName)
                    }
                    isMap -> {
                        val classKeyMap = Class.forName(tempData?.getDataKeyMapClassName())
                        val typeMap = jsonMapper.newParameterizedType(collectionClassName, classKeyMap, dataClassName)
                        typeRecord = jsonMapper.newParameterizedType(Record::class.java, typeMap)
                    }
                    else -> {
                        typeRecord = jsonMapper.newParameterizedType(Record::class.java, dataClassName)
                    }
                }
                val diskRecord: Record<T>? = jsonMapper.fromJson(resultedFile.absoluteFile, typeRecord)
                diskRecord?.sizeOnMb = (resultedFile.length().toFloat() / 1024 / 1024)
                return diskRecord
            } catch (exception: Exception) {
                return null
            }
        }
    }

    private fun safetyKey(key: String) = key.replace("/", "_")

}