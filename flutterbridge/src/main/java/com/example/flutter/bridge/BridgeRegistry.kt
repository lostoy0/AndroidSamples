package com.example.flutter.bridge

import java.util.concurrent.CompletionStage

internal class BridgeRegistry(plugins: Iterable<BridgePlugin>) {
    private val methods: MutableMap<String?, BridgePlugin.Binding> =
        HashMap()

    init {
        for (plugin in plugins) {
            for (binding in plugin.methods()) {
                require(
                    methods.putIfAbsent(
                        binding.name,
                        binding
                    ) == null
                ) { "Duplicate bridge method: " + binding.name }
            }
        }
    }

    fun dispatch(name: String?, args: Any?): CompletionStage<Any?> {
        val binding = methods.get(name)
        if (binding == null) throw BridgeException(
            "not_implemented",
            "Unknown bridge method: " + name
        )
        return binding.handler.apply(args)
    }
}
