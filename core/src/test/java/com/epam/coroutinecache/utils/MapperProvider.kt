package com.epam.coroutinecache.utils

import com.epam.coroutinecache.mappers.GsonMapper
import com.epam.coroutinecache.mappers.JacksonMapper
import com.epam.coroutinecache.mappers.JsonMapper
import com.epam.coroutinecache.mappers.MoshiMapper

class MapperProvider {

    fun provideMapperByChooser(chooser: JsonFactoryChooser): JsonMapper {
        return when (chooser) {
            JsonFactoryChooser.MOSHI -> MoshiMapper()
            JsonFactoryChooser.GSON -> GsonMapper()
            JsonFactoryChooser.JACKSON -> JacksonMapper()
        }
    }

    fun provideMoshiMapper(): JsonMapper = MoshiMapper()

    fun provideGsonMapper(): JsonMapper = GsonMapper()

    fun provideJacksonMapper(): JsonMapper = JacksonMapper()
}