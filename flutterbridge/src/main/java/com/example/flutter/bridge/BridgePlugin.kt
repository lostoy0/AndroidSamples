package com.example.flutter.bridge

import java.util.concurrent.CompletionStage
import java.util.function.Function

/** A group of methods provided by a host feature.  */
fun interface BridgePlugin {
    fun methods(): List<Binding>

    class Binding internal constructor(
        val name: String,
        val handler: Function<Any?, CompletionStage<Any?>>
    )
}
