package com.epam.example

import com.epam.example.core.Record
import com.epam.example.utils.MockDataString
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import org.junit.Test

class MemoryTest: BaseTest() {

    @Test
    fun testSaveAndRetrieveData() {
        val data1 = MockDataString(STRING_VALUE)
        val data2 = MockDataString(STRING_VALUE + 1)
        memory.saveRecord(KEY, Record(data1))
        memory.saveRecord(KEY + 1, Record(data2))

        assertTrue(memory.keySet().size == 2)
        assertEquals(memory.getRecord<MockDataString>(KEY)?.getData(), data1)
        assertEquals(memory.getRecord<MockDataString>(KEY + 1)?.getData(), data2)
    }

    @Test
    fun testDeleteByKey() {
        val data1 = MockDataString(STRING_VALUE)
        val data2 = MockDataString(STRING_VALUE + 1)
        memory.saveRecord(KEY, Record(data1))
        memory.saveRecord(KEY + 1, Record(data2))

        memory.deleteByKey(KEY)

        assertTrue(memory.keySet().size == 1)
        assertEquals(memory.getRecord<MockDataString>(KEY)?.getData(), null)
        assertEquals(memory.getRecord<MockDataString>(KEY + 1)?.getData(), data2)
    }

    @Test
    fun testDeleteAllRecords() {
        val data1 = MockDataString(STRING_VALUE)
        val data2 = MockDataString(STRING_VALUE + 1)
        memory.saveRecord(KEY, Record(data1))
        memory.saveRecord(KEY + 1, Record(data2))

        memory.deleteAll()

        assertTrue(memory.keySet().isEmpty())
    }

    companion object {
        private const val STRING_VALUE = "Test value"
        private const val KEY = "key"
    }
}