package com.epam.example.utils

import com.epam.example.mappers.GsonMapper
import com.epam.example.mappers.JacksonMapper
import com.epam.example.mappers.JsonMapper
import com.epam.example.mappers.MoshiMapper

class MapperProvider {

    fun provideMapperByChooser(chooser: JsonFactoryChooser): JsonMapper {
        return when(chooser) {
            JsonFactoryChooser.MOSHI -> MoshiMapper()
            JsonFactoryChooser.GSON -> GsonMapper()
            JsonFactoryChooser.JACKSON -> JacksonMapper()
        }
    }

    fun provideMoshiMapper(): JsonMapper = MoshiMapper()

    fun provideGsonMapper(): JsonMapper = GsonMapper()

    fun provideJacksonMapper(): JsonMapper = JacksonMapper()
}