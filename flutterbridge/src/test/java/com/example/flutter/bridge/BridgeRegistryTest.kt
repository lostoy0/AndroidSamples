package com.example.flutter.bridge

import java.util.concurrent.CompletableFuture
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class BridgeRegistryTest {
    private val echo = BridgeMethod("demo.echo", BridgeMethod.Companion::string, BridgeMethod.Companion::string)
    private val plugin = BridgePlugin {
        listOf(echo.bind { CompletableFuture.completedFuture("echo: $it") })
    }

    @Test
    fun dispatchesTypedMethodsAndRejectsInvalidArguments() {
        val registry = BridgeRegistry(listOf(plugin))
        assertEquals("echo: hello", registry.dispatch(echo.name, "hello").toCompletableFuture().get())
        assertEquals("invalid_argument", assertThrows(BridgeException::class.java) {
            registry.dispatch(echo.name, 42)
        }.code)
        assertEquals("not_implemented", assertThrows(BridgeException::class.java) {
            registry.dispatch("missing.method", null)
        }.code)
    }

    @Test
    fun rejectsDuplicateNames() {
        assertThrows(IllegalArgumentException::class.java) { BridgeRegistry(listOf(plugin, plugin)) }
    }
}
