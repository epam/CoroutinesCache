package com.epam.example

import com.epam.example.core.Memory
import com.epam.example.core.Persistence
import com.epam.example.di.actionsModule
import com.epam.example.di.cacheModule
import com.epam.example.utils.JsonFactoryChooser
import com.epam.example.utils.MapperProvider
import org.junit.*
import org.junit.rules.TemporaryFolder
import org.koin.core.parameter.parametersOf
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.StandAloneContext.stopKoin
import org.koin.standalone.get
import org.koin.test.KoinTest
import org.koin.test.checkModules
import org.koin.test.dryRun

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