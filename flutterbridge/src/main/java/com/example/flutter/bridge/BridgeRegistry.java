package com.example.flutter.bridge;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletionStage;

final class BridgeRegistry {
    private final Map<String, BridgePlugin.Binding> methods = new HashMap<>();

    BridgeRegistry(Iterable<BridgePlugin> plugins) {
        for (BridgePlugin plugin : plugins) {
            for (BridgePlugin.Binding binding : plugin.methods()) {
                if (methods.putIfAbsent(binding.name, binding) != null) {
                    throw new IllegalArgumentException("Duplicate bridge method: " + binding.name);
                }
            }
        }
    }

    CompletionStage<Object> dispatch(String name, Object args) {
        BridgePlugin.Binding binding = methods.get(name);
        if (binding == null) throw new BridgeException("not_implemented", "Unknown bridge method: " + name);
        return binding.handler.apply(args);
    }
}
