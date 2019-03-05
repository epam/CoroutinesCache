package com.epam.coroutinecache

import com.epam.coroutinecache.core.Record
import com.epam.coroutinecache.utils.MockDataString
import org.junit.*

import org.junit.Assert.*

class DiskTest: BaseTest() {


    @Test
    fun testRetrieveRecordAsObject() {
        diskCache.saveRecord(KEY, Record(MockDataString(VALUE_STRING)))

        val retrievedData = diskCache.getRecord<MockDataString>(KEY)
        assertEquals(retrievedData?.getData()?.getMessage(), VALUE_STRING)
    }

    @Test
    fun testRetrieveRecordAsCollection() {
        val mocks = listOf(MockDataString(VALUE_STRING), MockDataString(VALUE_STRING + 1))
        diskCache.saveRecord(KEY, Record(mocks))

        val retrievedData = diskCache.getRecord<List<MockDataString>>(KEY)
        for (i in 0 until mocks.size) {
            assertEquals(mocks[i], retrievedData?.getData()?.get(i))
        }
    }

    @Test
    fun testRetrieveRecordAsArray() {
        val mocks = arrayOf(MockDataString(VALUE_STRING), MockDataString(VALUE_STRING + 1))
        diskCache.saveRecord(KEY, Record(mocks))

        val retrievedData = diskCache.getRecord<Array<MockDataString>>(KEY)
        assertArrayEquals(mocks, retrievedData?.getData())
    }

    @Test
    fun testRetrieveRecordAsMap() {
        val testMap = HashMap<Int, MockDataString>()
        testMap[0] = MockDataString(VALUE_STRING)
        testMap[1] = MockDataString(VALUE_STRING + 1)
        diskCache.saveRecord(KEY, Record(testMap))

        val retrievedData = diskCache.getRecord<Map<Int, MockDataString>>(KEY)
        for ((k, v) in testMap) {
            assertEquals(v, retrievedData?.getData()?.get(k))
        }
    }

    @Test
    fun testRetrieveRecordAsMultilevelMap() {
        val innerMap = HashMap<Any, Any>()
        innerMap["test"] = VALUE_STRING
        innerMap["test2"] = VALUE_STRING + 1

        val testMap = HashMap<Any, Any>()
        testMap[0] = VALUE_STRING + 2
        testMap[1] = VALUE_STRING + 3
        testMap[2] = innerMap

        diskCache.saveRecord(KEY, Record(testMap))

        val retrievedData = diskCache.getRecord<Map<Any, Any>>(KEY)
        assertEquals(retrievedData?.getData()?.get(0), VALUE_STRING + 2)
        assertEquals(retrievedData?.getData()?.get(1), VALUE_STRING + 3)

        val mapObject = retrievedData?.getData()?.get(2)
        assertTrue(mapObject is Map<*, *>)
        mapObject as Map<*, *>
        assertEquals(mapObject["test"], VALUE_STRING)
        assertEquals(mapObject["test2"], VALUE_STRING + 1)
    }

    @Test
    fun testRemoveKeys() {
        for (i in 0 until 100) {
            diskCache.saveRecord(i.toString(), Record(VALUE_STRING))
        }

        assertEquals(diskCache.allKeys().size, 100)
        diskCache.deleteAll()
        assertEquals(diskCache.allKeys().size, 0)
    }

    companion object {
        private const val KEY: String = "store/key"
        private const val VALUE_STRING: String = "Stored data"
    }
}