package com.soywiz.korinject

import com.soywiz.korinject.internal.*
import kotlinx.serialization.*
import kotlinx.serialization.modules.*
import kotlin.reflect.*

fun AsyncInjector.serializationAutomapping(): AsyncInjector = this.apply {
    this.typeFallbackProvider = { type, ctx -> serializationAutomappingFallback(type, ctx) }
}

private fun serializationAutomappingFallback(type: KType, ctx: AsyncInjector.RequestContext): AsyncObjectProvider<*> {
    val clazz = type.kclass()
    println(clazz)
    //val serializer = EmptySerializersModule.serializer(kclazz) ?: error("Can't find class $kclazz for serialization")
    val serializer = EmptySerializersModule.serializer(type)

    return object : AsyncObjectProvider<Any> {
        override suspend fun get(injector: AsyncInjector): Any {
            TODO("Not yet implemented")
        }

        override suspend fun deinit() {
            TODO("Not yet implemented")
        }
    }
}
