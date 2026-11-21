package com.example.flutter.bridge

import java.nio.charset.CharacterCodingException

import android.app.Activity
import java.io.ByteArrayOutputStream
import java.net.URI
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.util.Collections
import java.util.function.Consumer

/** Registers and resolves the in-app URL scheme before any screen is opened.  */
class RouteTable {
    fun interface NativeDestination {
        fun open(activity: Activity, route: Request)
    }

    class UnknownRoute internal constructor(url: String?) :
        IllegalArgumentException("No page is registered for " + url)

    class Request internal constructor(
        val url: String,
        path: MutableMap<String?, String?>,
        query: MutableMap<String?, String?>
    ) {
        val path: MutableMap<String?, String?>
        @JvmField
        val query: MutableMap<String?, String?>

        init {
            this.path = Collections.unmodifiableMap<String?, String?>(path)
            this.query = Collections.unmodifiableMap<String?, String?>(query)
        }

        fun positiveInt(key: String?): Int {
            val value = path.get(key)
            if (value != null && value.matches("[1-9][0-9]*".toRegex())) {
                try {
                    return value.toInt()
                } catch (ignored: NumberFormatException) {
                    // Report overflows the same way as other invalid IDs.
                }
            }
            throw IllegalArgumentException("Expected a positive integer for " + key)
        }
    }

    class Match internal constructor(
        @JvmField val request: Request,
        @JvmField val nativeDestination: NativeDestination?
    )

    private class Entry(
        val host: String, val template: Array<String>, val validator: Consumer<Request>,
        val destination: NativeDestination?
    )

    private val entries: MutableList<Entry> = ArrayList<Entry>()

    fun flutter(template: String, validator: Consumer<Request>): RouteTable {
        return register("flutter", template, validator, null)
    }

    fun nativePage(
        template: String, validator: Consumer<Request>,
        destination: NativeDestination
    ): RouteTable {
        requireNotNull(destination) { "Native destination required" }
        return register("native", template, validator, destination)
    }

    private fun register(
        host: String, template: String, validator: Consumer<Request>,
        destination: NativeDestination?
    ): RouteTable {
        val segments: Array<String> = segments(template)
        for (entry in entries) {
            require(
                !(entry.host == host && overlaps(
                    entry.template,
                    segments
                ))
            ) { "Overlapping route template: " + template }
        }
        entries.add(Entry(host, segments, validator, destination))
        return this
    }

    fun resolve(url: String?): Match {
        requireNotNull(url) { "A URL is required" }
        val uri = URI.create(url)
        val host = uri.getRawAuthority()
        require(!(("samples" != uri.getScheme()) || uri.getRawFragment() != null || ("native" != host && "flutter" != host))) { "Expected an in-app samples URL" }

        val path: Array<String> = segments(uri.getRawPath())
        for (i in path.indices) {
            val segment: String = decode(path[i], false)
            require(
                !(segment.contains("/") || segment.contains("\\")
                        || segment == "." || segment == "..")
            ) { "Invalid URL path segment" }
            path[i] = segment
        }
        val query: MutableMap<String?, String?> = query(uri.getRawQuery())
        for (entry in entries) {
            if (entry.host != host || entry.template.size != path.size) continue
            val params: MutableMap<String?, String?>? = match(entry.template, path)
            if (params == null) continue
            val request = Request(uri.toASCIIString(), params, query)
            entry.validator.accept(request)
            return Match(request, entry.destination)
        }
        throw UnknownRoute(url)
    }

    companion object {
        private fun variable(segment: String): Boolean {
            return segment.startsWith("{") && segment.endsWith("}")
        }

        private fun overlaps(first: Array<String>, second: Array<String>): Boolean {
            if (first.size != second.size) return false
            for (i in first.indices) {
                if (!variable(first[i]) && !variable(second[i]) && (first[i] != second[i])) {
                    return false
                }
            }
            return true
        }

        private fun match(
            template: Array<String>,
            path: Array<String>
        ): MutableMap<String?, String?>? {
            val params: MutableMap<String?, String?> = LinkedHashMap<String?, String?>()
            for (i in template.indices) {
                val part = template[i]
                if (variable(part)) {
                    params.put(part.substring(1, part.length - 1), path[i])
                } else if (part != path[i]) {
                    return null
                }
            }
            return params
        }

        private fun segments(path: String?): Array<String> {
            require(!(path == null || !path.startsWith("/") || path.endsWith("/") || path.contains("//"))) { "Expected a non-empty URL path" }
            return path.substring(1).split("/".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
        }

        private fun query(raw: String?): MutableMap<String?, String?> {
            val result: MutableMap<String?, String?> = LinkedHashMap<String?, String?>()
            if (raw == null || raw.isEmpty()) return result
            for (pair in raw.split("&".toRegex()).toTypedArray()) {
                val separator = pair.indexOf('=')
                require(separator >= 1) { "Expected query key=value" }
                val key: String = decode(pair.substring(0, separator), true)
                require(
                    result.put(
                        key,
                        decode(pair.substring(separator + 1), true)
                    ) == null
                ) { "Repeated query parameter: " + key }
            }
            return result
        }

        private fun decode(raw: String, query: Boolean): String {
            val decoded = StringBuilder()
            var index = 0
            while (index < raw.length) {
                val character = raw.get(index)
                if (character != '%') {
                    decoded.append(if (query && character == '+') ' ' else character)
                    index++
                    continue
                }

                val bytes = ByteArrayOutputStream()
                while (index < raw.length && raw.get(index) == '%') {
                    // URI.create already checked the syntax of each percent escape.
                    bytes.write(raw.substring(index + 1, index + 3).toInt(16))
                    index += 3
                }
                try {
                    decoded.append(
                        StandardCharsets.UTF_8.newDecoder()
                            .decode(ByteBuffer.wrap(bytes.toByteArray()))
                    )
                } catch (error: CharacterCodingException) {
                    throw IllegalArgumentException("Invalid UTF-8 in the URL", error)
                }
            }
            return decoded.toString()
        }
    }
}
