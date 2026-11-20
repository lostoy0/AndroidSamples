package com.example.flutter.bridge;

import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

public final class BridgeMethod<A, R> {
    public final String name;
    private final Function<Object, A> decodeArgs;
    private final Function<Object, R> decodeResult;
    private final Function<A, Object> encodeArgs;
    private final Function<R, Object> encodeResult;

    public BridgeMethod(String name, Function<Object, A> decodeArgs,
            Function<Object, R> decodeResult) {
        this(name, decodeArgs, decodeResult, value -> value, value -> value);
    }

    public BridgeMethod(String name, Function<Object, A> decodeArgs,
            Function<Object, R> decodeResult, Function<A, Object> encodeArgs,
            Function<R, Object> encodeResult) {
        this.name = name;
        this.decodeArgs = decodeArgs;
        this.decodeResult = decodeResult;
        this.encodeArgs = encodeArgs;
        this.encodeResult = encodeResult;
    }

    public BridgePlugin.Binding bind(Function<A, CompletionStage<R>> handler) {
        return new BridgePlugin.Binding(name, payload ->
                handler.apply(decode(decodeArgs, payload, "invalid_argument")).thenApply(encodeResult));
    }

    Object encode(A args) {
        return encodeArgs.apply(args);
    }

    R decode(Object result) {
        return decode(decodeResult, result, "invalid_result");
    }

    private static <T> T decode(Function<Object, T> decoder, Object payload, String code) {
        try {
            return decoder.apply(payload);
        } catch (IllegalArgumentException error) {
            throw new BridgeException(code, error.getMessage());
        }
    }

    public static String string(Object value) {
        if (value instanceof String) return (String) value;
        throw new IllegalArgumentException("Expected a string");
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> map(Object value) {
        if (value instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) value;
            for (Object key : map.keySet()) {
                if (!(key instanceof String)) throw new IllegalArgumentException("Expected string keys");
            }
            return (Map<String, Object>) map;
        }
        throw new IllegalArgumentException("Expected a map");
    }
}
