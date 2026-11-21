package com.example.flutter.bridge

import java.util.concurrent.CompletionStage

class BridgeMethod<A, R>(
    val name: String,
    private val decodeArgs: (Any?) -> A,
    private val decodeResult: (Any?) -> R,
    private val encodeArgs: (A) -> Any? = { it },
    private val encodeResult: (R) -> Any? = { it }
) {
    fun bind(handler: (A) -> CompletionStage<R>): BridgePlugin.Binding =
        BridgePlugin.Binding(name) { payload ->
            handler(decode(decodeArgs, payload, "invalid_argument")).thenApply(encodeResult)
        }

    fun encode(args: A): Any? = encodeArgs(args)

    fun decode(result: Any?): R = decode(decodeResult, result, "invalid_result")

    companion object {
        private fun <T> decode(decoder: (Any?) -> T, payload: Any?, code: String): T {
            try {
                return decoder(payload)
            } catch (error: IllegalArgumentException) {
                throw BridgeException(code, error.message)
            }
        }

        fun string(value: Any?): String =
            value as? String ?: throw IllegalArgumentException("Expected a string")

        fun map(value: Any?): Map<String, Any?> {
            require(value is Map<*, *>) { "Expected a map" }
            require(value.keys.all { it is String }) { "Expected string keys" }
            @Suppress("UNCHECKED_CAST")
            return value as Map<String, Any?>
        }
    }
}
