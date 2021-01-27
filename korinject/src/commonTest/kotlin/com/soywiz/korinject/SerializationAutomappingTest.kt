package com.soywiz.korinject

import com.soywiz.korinject.internal.*
import com.soywiz.korinject.util.*
import kotlinx.serialization.*
import kotlinx.serialization.builtins.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import kotlinx.serialization.modules.*
import kotlin.reflect.*
import kotlin.test.*

@OptIn(ExperimentalStdlibApi::class)
class SerializationAutomappingTest {
    @Test
    fun test() = suspendTest {
        //val injector = AsyncInjector().serializationAutomapping()
        val injector = Injector2()
        injector.mapInstance("hello")
        val demo = injector.get<Demo>()
        println("DEMO: $demo")
    }

    @Serializable
    @Singleton
    data class Demo(val test: String)

    @Serializable
    @Singleton
    data class Demo2(val test2: String)
}

@OptIn(ExperimentalStdlibApi::class)
class Injector2(val parent: Injector2? = null) {
    inline fun <reified T : Any> get(): T = get(typeOf<T>()) as T

    private val instances = LinkedHashMap<KClass<*>, Any>()

    inline fun <reified T : Any> mapInstance(value: T): Injector2 = mapInstance(typeOf<T>(), value)

    fun <T : Any> mapInstance(type: KType, value: T): Injector2 = this.apply { instances[type.kclass()] = value }

    fun get(type: KType): Any {
        val clazz = type.kclass()
        return getOrNull(type, clazz) ?: error("Can't find ${clazz.qualifiedName} in the injector")
    }

    fun getOrNull(type: KType): Any? {
        return getOrNull(type, type.kclass())
    }

    private fun getOrNull(type: KType, clazz: KClass<*>): Any? {
        val instance = instances[clazz] ?: parent?.getOrNull(type, clazz)
        if (instance != null) return instance

        val result = createOrNull(type, clazz)
        if (result.singleton) {
            // @TODO: Where to store?
            instances[clazz] = result.instance
        }

        return result.instance
    }

    class CreateResult(val instance: Any, val singleton: Boolean)

    private fun createOrNull(type: KType, clazz: KClass<*>): CreateResult {
        val serializer = EmptySerializersModule.serializer(type)
        //val serializer = serializer(type)
        val module = serializersModuleOf(serializer as KSerializer<Any>)

        module.dumpTo(object : SerializersModuleCollector {
            override fun <T : Any> contextual(kClass: KClass<T>, serializer: KSerializer<T>) {
                println("contextual: $kClass, serializer=$serializer")
            }

            override fun <Base : Any, Sub : Base> polymorphic(
                baseClass: KClass<Base>,
                actualClass: KClass<Sub>,
                actualSerializer: KSerializer<Sub>
            ) {
                TODO("Not yet implemented")
            }

            override fun <Base : Any> polymorphicDefault(
                baseClass: KClass<Base>,
                defaultSerializerProvider: (className: String?) -> DeserializationStrategy<out Base>?
            ) {
                TODO("Not yet implemented")
            }
        })

        println(module)
        val instance = InjectorDecoder().decodeSerializableValue(serializer)
        return CreateResult(instance!!, singleton = true)
    }

    inner class InjectorDecoder(
        override val serializersModule: SerializersModule = EmptySerializersModule
    ) : AbstractDecoder() {
        var index = 0

        override fun decodeValue(): Any {
            return super.decodeValue()
        }

        override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
            println("descriptor: $descriptor")
            val nindex = index++
            return if (nindex >= descriptor.elementsCount) CompositeDecoder.DECODE_DONE else nindex
        }

    }
}
