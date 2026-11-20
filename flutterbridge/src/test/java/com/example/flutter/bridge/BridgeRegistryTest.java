package com.example.flutter.bridge;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.*;

public class BridgeRegistryTest {
    private final BridgeMethod<String, String> echo =
            new BridgeMethod<>("demo.echo", BridgeMethod::string, BridgeMethod::string);
    private final BridgePlugin plugin = () -> Collections.singletonList(
            echo.bind(value -> CompletableFuture.completedFuture("echo: " + value)));

    @Test
    public void dispatchesTypedMethodsAndRejectsInvalidArguments() throws Exception {
        BridgeRegistry registry = new BridgeRegistry(Collections.singletonList(plugin));
        assertEquals("echo: hello", registry.dispatch(echo.name, "hello").toCompletableFuture().get());
        assertEquals("invalid_argument",
                assertThrows(BridgeException.class, () -> registry.dispatch(echo.name, 42)).code);
        assertEquals("not_implemented",
                assertThrows(BridgeException.class, () -> registry.dispatch("missing.method", null)).code);
    }

    @Test
    public void rejectsDuplicateNames() {
        assertThrows(IllegalArgumentException.class,
                () -> new BridgeRegistry(Arrays.asList(plugin, plugin)));
    }
}
