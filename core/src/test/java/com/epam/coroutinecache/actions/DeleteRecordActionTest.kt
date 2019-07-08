package com.epam.coroutinecache.actions

import com.epam.coroutinecache.BaseTest
import com.epam.coroutinecache.core.Record
import com.epam.coroutinecache.core.actions.DeleteRecordAction
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test
import org.koin.core.parameter.parametersOf
import org.koin.standalone.inject
import java.lang.reflect.Type

class DeleteRecordActionTest : BaseTest() {

    protected val deleteAction: DeleteRecordAction by inject { parametersOf(GlobalScope) }

    @Test
    fun testDeleteRecord() {
        runBlocking {
            saveData(KEY, Record(DATA), DATA::class.javaObjectType)
            deleteAction.deleteByKey(KEY)

            assertCacheSize(0)

            saveData(KEY, Record(DATA), DATA::class.javaObjectType)
            saveData(KEY + 1, Record(DATA), DATA::class.javaObjectType)
            saveData(KEY + 2, Record(DATA), DATA::class.javaObjectType)
            deleteAction.deleteByKey(KEY)

            assertCacheSize(2)
        }
    }

    @Test
    fun testDeleteAllRecords() {
        runBlocking {
            for (i in 0 until 100) {
                saveData(KEY + i, Record(DATA), DATA::class.javaObjectType)
            }
            deleteAction.deleteAll()

            assertCacheSize(0)
        }
    }

    private fun saveData(key: String, record: Record<*>, type: Type) {
        diskCache.saveRecord(key, record, type)
        memory.saveRecord(key, record)
    }

    private fun assertCacheSize(size: Int) {
        assertTrue(diskCache.allKeys().size == size)
        assertTrue(memory.keySet().size == size)
    }

    companion object {
        private const val KEY = "KEY"
        private const val DATA = "DATA"
    }
}