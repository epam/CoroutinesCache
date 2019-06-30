package com.epam.coroutinecache.core

import com.epam.coroutinecache.mappers.JsonMapper
import java.io.File
import java.io.FileWriter
import java.lang.Exception
import java.lang.reflect.Type

class DiskCache(
        private external val cacheDirectory: File,
        private val jsonMapper: JsonMapper
): Persistence {

    override fun <T> saveRecord(key: String, record: Record<T>, entryType: Type) = synchronized(this) {
        val safetyKey = safetyKey(key)
        val type = jsonMapper.newParameterizedType(Record::class.java, entryType)
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
            return (result.toFloat() / sizeMb).toLong()
        }
    }

    override fun <T> getRecord(key: String, entryType: Type): Record<T>? {
        synchronized(this) {
            return try {
                val safetyKey = safetyKey(key)
                val resultedFile = File(cacheDirectory, safetyKey)
                val type = jsonMapper.newParameterizedType(Record::class.java, entryType)
                val diskRecord: Record<T>? = jsonMapper.fromJson(resultedFile, type)
                diskRecord?.sizeOnMb = (resultedFile.length().toFloat() / sizeMb)
                diskRecord
            } catch (exception: Exception) {
                null
            }
        }
    }

    private fun safetyKey(key: String) = key.replace("/", "_")

    companion object {
        private const val sizeKb = 1024.0f
        private const val sizeMb = sizeKb * sizeKb
    }
}