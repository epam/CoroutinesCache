package com.epam.coroutinecache.actions

import com.epam.coroutinecache.BaseTest
import com.epam.coroutinecache.core.Record
import com.epam.coroutinecache.core.Source
import com.epam.coroutinecache.core.actions.GetRecordAction
import com.epam.coroutinecache.core.actions.SaveRecordAction
import com.epam.coroutinecache.utils.MockDataString
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.koin.core.parameter.parametersOf
import org.koin.standalone.inject

class GetRecordActionTest : BaseTest() {

    private val saveRecordAction: SaveRecordAction by inject { parametersOf(MAX_MB_CACHE_SIZE, GlobalScope) }

    private val getRecordAction: GetRecordAction by inject { parametersOf(GlobalScope) }


    @Test
    fun testGetSingleRecordFromMemoryAndPersistence() {
        runBlocking {
            val savingData = createMockList()
            saveRecordAction.save(KEY, savingData)

            var retrievedData: Record<Any>? = getRecordAction.getRecord(KEY)
            checkRetrievedDataIsCorrect(Source.MEMORY, savingData, retrievedData)

            memory.deleteByKey(KEY)

            retrievedData = getRecordAction.getRecord(KEY)
            checkRetrievedDataIsCorrect(Source.PERSISTENCE, savingData, retrievedData)
        }
    }

    @Test
    fun testGetMultiplyRecordsFromMemoryAndPersistence() {
        runBlocking {
            val savingData = createMockList()
            for (i in 0 until MAX_RECORDS) {
                saveRecordAction.save(KEY + i, savingData)
            }
            for (i in 0 until MAX_RECORDS) {
                val retrievedData: Record<Any>? = getRecordAction.getRecord(KEY + i)
                checkRetrievedDataIsCorrect(Source.MEMORY, savingData, retrievedData)
            }
            memory.deleteAll()
            for (i in 0 until MAX_RECORDS) {
                val retrievedData: Record<Any>? = getRecordAction.getRecord(KEY + i)
                checkRetrievedDataIsCorrect(Source.PERSISTENCE, savingData, retrievedData)
            }
        }
    }

    @Test
    fun testGetExpiredDataFromMemory() {
        runBlocking {
            val savingData = createMockList()
            saveRecordAction.save(KEY, savingData, 1000)

            delay(1500)

            var retrievedData: Record<Any>? = getRecordAction.getRecord(KEY, true)
            checkRetrievedDataIsCorrect(Source.MEMORY, savingData, retrievedData)

            retrievedData = getRecordAction.getRecord(KEY, true)
            assertEquals(retrievedData, null)
        }
    }

    @Test
    fun testGetExpiredDataFromPersistence() {
        runBlocking {
            val savingData = createMockList()
            saveRecordAction.save(KEY, savingData, 1000)

            delay(1500)
            memory.deleteByKey(KEY)

            var retrievedData: Record<Any>? = getRecordAction.getRecord(KEY, true)
            checkRetrievedDataIsCorrect(Source.PERSISTENCE, savingData, retrievedData)

            retrievedData = getRecordAction.getRecord(KEY, true)
            assertEquals(retrievedData, null)
        }
    }

    private fun createMockList(): List<MockDataString> {
        val result = arrayListOf<MockDataString>()
        for (i in 0 until RECORDS_COUNT) {
            result.add(MockDataString(RECORD_DATA))
        }
        return result
    }

    private fun checkRetrievedDataIsCorrect(source: Source, savingData: List<MockDataString>, retrievedData: Record<Any>?) {
        assertTrue(retrievedData?.getSource() == source)

        val data = retrievedData?.getData()
        assertTrue(data is List<*> && data.size == savingData.size)
        data as List<*>
        for (i in 0 until savingData.size) {
            assertEquals(savingData[i], data[i])
        }
    }


    companion object {
        private const val MAX_MB_CACHE_SIZE: Int = 20
        private const val RECORDS_COUNT = 1000
        private const val MAX_RECORDS = 20
        private const val KEY = "DATA_KEY"
        private const val RECORD_DATA = "Lorem ipsum dolor sit amet, volutpat velit adipiscing ligula lorem tortor mauris, vel ipsum porttitor vivamus nec, nascetur augue." +
                " Integer ut et, consequat ac urna, pede elementum ut vitae orci." +
                " Sed lorem sodales nam viverra semper, curabitur suscipit ut suscipit proin lectus facilisis, donec sapien facilisis volutpat, aliquam adipiscing consectetuer mauris neque quam, laoreet in." +
                " Nunc augue quis per vestibulum, neque curabitur egestas hymenaeos, diam pede. Dolor lacus elit ultricies pellentesque sed. Ante amet ipsum duis sit est integer."
    }
}