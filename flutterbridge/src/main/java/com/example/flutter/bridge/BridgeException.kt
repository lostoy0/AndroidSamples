package com.example.flutter.bridge

import java.util.concurrent.CompletionException

class BridgeException(@JvmField val code: String, message: String?) : RuntimeException(message) {
    companion object {
        fun from(error: Throwable): BridgeException {
            var error = error
            while (error is CompletionException && error.cause != null) {
                error = error.cause!!
            }
            if (error is BridgeException) return error
            return BridgeException("handler_error", "The bridge handler failed")
        }
    }
}
