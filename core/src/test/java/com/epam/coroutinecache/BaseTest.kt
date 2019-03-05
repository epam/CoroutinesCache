package com.epam.coroutinecache

import com.epam.coroutinecache.core.Memory
import com.epam.coroutinecache.core.Persistence
import com.epam.coroutinecache.di.actionsModule
import com.epam.coroutinecache.di.cacheModule
import com.epam.coroutinecache.utils.JsonFactoryChooser
import com.epam.coroutinecache.utils.MapperProvider
import org.junit.*
import org.junit.rules.TemporaryFolder
import org.koin.core.parameter.parametersOf
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.StandAloneContext.stopKoin
import org.koin.standalone.get
import org.koin.test.KoinTest

open class BaseTest : KoinTest {

    @Rule
    @JvmField
    val temporaryFolder = TemporaryFolder()

    private val mapperProvider = MapperProvider()

    lateinit var memory: Memory

    lateinit var diskCache: Persistence

    @Before
    fun beforeTest() {
        startKoin(arrayListOf(cacheModule, actionsModule))
        memory = get()
        diskCache = get { parametersOf(temporaryFolder.root, mapperProvider.provideMapperByChooser(JsonFactoryChooser.MOSHI)) }
    }

    @After
    fun afterTest() {
        stopKoin()
    }
}