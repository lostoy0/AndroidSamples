package com.example.flutter.bridge

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test

class RouteTableTest {
    private val routes = RouteTable()
        .flutter("/detail/{id}") { it.positiveInt("id") }
        .flutter("/communications") {}

    @Test
    fun decodesEncodedQueryAndMatchesTemplate() {
        val match = routes.resolve("samples://flutter/detail/42?title=%E4%B8%AD%2B%26%2F")
        assertEquals(42, match.request.positiveInt("id"))
        assertEquals("中+&/", match.request.query["title"])
        assertNull(match.nativeDestination)
    }

    @Test
    fun rejectsInvalidParametersAndUrls() {
        val urls = listOf(
            "samples://flutter/detail/42?title=%FF",
            "samples://flutter/detail/0",
            "samples://flutter/detail/42?title=a&title=b",
            "samples://flutter/detail/a%2Fb",
            "samples://flutter/detail/1#fragment",
            "samples://user@flutter/communications",
            "https://flutter/communications"
        )
        for (url in urls) {
            assertThrows(url, IllegalArgumentException::class.java) { routes.resolve(url) }
        }
    }

    @Test
    fun reportsMissingRouteAndDuplicateRegistration() {
        assertThrows(RouteTable.UnknownRoute::class.java) {
            routes.resolve("samples://flutter/missing")
        }
        assertThrows(IllegalArgumentException::class.java) { routes.flutter("/detail/{other}") {} }
    }
}
