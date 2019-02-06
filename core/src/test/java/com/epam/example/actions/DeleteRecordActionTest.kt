package com.epam.example.actions

import com.epam.example.BaseTest
import com.epam.example.core.Record
import com.epam.example.core.actions.DeleteRecordAction
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.koin.core.parameter.parametersOf
import org.koin.standalone.inject

class DeleteRecordActionTest : BaseTest() {

    protected val deleteAction: DeleteRecordAction by inject { parametersOf(GlobalScope) }

    @Test
    fun testDeleteRecord() {
        runBlocking {
            saveData(KEY, Record(DATA))
            deleteAction.deleteByKey(KEY)

            assertCacheSize(0)

            saveData(KEY, Record(DATA))
            saveData(KEY + 1, Record(DATA))
            saveData(KEY + 2, Record(DATA))
            deleteAction.deleteByKey(KEY)

            assertCacheSize(2)
        }
    }

    @Test
    fun testDeleteAllRecords() {
        runBlocking {
            for (i in 0 until 100) {
                saveData(KEY + i, Record(DATA))
            }
            deleteAction.deleteAll()

            assertCacheSize(0)
        }
    }

    private fun saveData(key: String, record: Record<*>) {
        diskCache.saveRecord(key, record)
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