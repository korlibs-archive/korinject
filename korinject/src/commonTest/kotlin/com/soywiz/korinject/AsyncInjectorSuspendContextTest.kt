package com.soywiz.korinject

import com.soywiz.korinject.util.*
import kotlin.test.*

class AsyncInjectorSuspendContextTest {
    @Test
    fun testWithInjectorStoresInjectorInTheContext() = suspendTest {
        val injector = AsyncInjector()
        val string = "hello"
        injector.mapInstance(string)
        val result = withInjector(injector) {
            otherFunction()
        }
        assertEquals(string, result)
    }

    suspend fun otherFunction(): String = injector().get()
}
