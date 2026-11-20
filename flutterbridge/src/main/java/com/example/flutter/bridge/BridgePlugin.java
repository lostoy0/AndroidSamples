package com.example.flutter.bridge;

import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

/** A group of methods provided by a host feature. */
public interface BridgePlugin {
    List<Binding> methods();

    final class Binding {
        final String name;
        final Function<Object, CompletionStage<Object>> handler;

        Binding(String name, Function<Object, CompletionStage<Object>> handler) {
            this.name = name;
            this.handler = handler;
        }
    }
}
