package com.epam.coroutinecache.actions

import com.epam.coroutinecache.BaseTest
import com.epam.coroutinecache.core.actions.SaveRecordAction
import com.epam.coroutinecache.utils.MockDataString
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.koin.core.parameter.parametersOf
import org.koin.standalone.inject

class SaveRecordActionTest : BaseTest() {

    private val saveAction: SaveRecordAction by inject { parametersOf(MAX_MB_CACHE_SIZE, GlobalScope) }

    @Test
    fun testSaveRecord() {
        runBlocking {
            for (i in 0 until RECORDS_COUNT) {
                saveAction.save(KEY + i, createMockList()).await()
            }
            assertTrue(diskCache.storedMB() <= MAX_MB_CACHE_SIZE)
            assertTrue(memory.keySet().size == RECORDS_COUNT)
        }
    }

    // Cache size is 20Mb. Each record is 0.5332918Mb. After reaching max available size, action should delete records for reaching cache's comfortable size
    @Test
    fun shouldDeleteRecordsIfMemoryIsFull() {
        val maxCount = (MAX_MB_CACHE_SIZE * MAX_AVAILABLE_SIZE_COEFF / RECORD_SIZE).toInt()
        runBlocking {
            for (i in 0 until maxCount) {
                saveAction.save(KEY + i, createMockList()).await()
            }
            assertTrue(diskCache.allKeys().size == maxCount)

            val expectedCountAfterDelete = (MAX_MB_CACHE_SIZE * COMFORTABLE_COEFF / RECORD_SIZE).toInt()
            saveAction.save(KEY + maxCount, createMockList()).await()

            assertTrue(diskCache.allKeys().size == expectedCountAfterDelete)
        }
    }

    // 0.5332918 Mb each list
    private fun createMockList(): List<MockDataString> {
        val result = arrayListOf<MockDataString>()
        for (i in 0 until RECORDS_COUNT) {
            result.add(MockDataString(RECORD_DATA))
        }
        return result
    }

    companion object {
        private const val MAX_MB_CACHE_SIZE: Int = 20
        private const val RECORDS_COUNT = 1000
        private const val KEY = "DATA_KEY"
        private const val RECORD_DATA = "Lorem ipsum dolor sit amet, volutpat velit adipiscing ligula lorem tortor mauris, vel ipsum porttitor vivamus nec, nascetur augue." +
                " Integer ut et, consequat ac urna, pede elementum ut vitae orci." +
                " Sed lorem sodales nam viverra semper, curabitur suscipit ut suscipit proin lectus facilisis, donec sapien facilisis volutpat, aliquam adipiscing consectetuer mauris neque quam, laoreet in." +
                " Nunc augue quis per vestibulum, neque curabitur egestas hymenaeos, diam pede. Dolor lacus elit ultricies pellentesque sed. Ante amet ipsum duis sit est integer."
        private const val RECORD_SIZE = 0.5332918
        private const val MAX_AVAILABLE_SIZE_COEFF = 0.95
        private const val COMFORTABLE_COEFF = 0.70
    }
}