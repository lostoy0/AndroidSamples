package com.example.flutter.bridge;

import android.os.Handler;
import android.os.Looper;

import io.flutter.plugin.common.BasicMessageChannel;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.StandardMessageCodec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/** One bridge per engine. Callers use it on the Android main thread. */
public final class BridgeSession implements AutoCloseable {
    public interface EventSource {
        Runnable subscribe(Consumer<Map<String, Object>> emit);
    }

    private static final String PREFIX = "android_samples/bridge/v1/";
    private static final long TIMEOUT_MS = 5000;

    private final Handler main = new Handler(Looper.getMainLooper());
    private final Set<CompletableFuture<?>> pending = new HashSet<>();
    private final MethodChannel control;
    private final MethodChannel rpc;
    private final BasicMessageChannel<Object> messages;
    private final EventChannel events;
    private final EventSource source;

    private boolean ready;
    private boolean active;
    private boolean closed;
    private EventChannel.EventSink sink;
    private Runnable unsubscribe;

    BridgeSession(BinaryMessenger messenger, HybridRuntime.Configuration config,
            Function<String, String> openRoute) {
        BridgeRegistry registry = new BridgeRegistry(config.plugins);
        source = config.events;
        control = new MethodChannel(messenger, PREFIX + "control");
        rpc = new MethodChannel(messenger, PREFIX + "rpc");
        messages = new BasicMessageChannel<>(messenger, PREFIX + "message",
                StandardMessageCodec.INSTANCE);
        events = new EventChannel(messenger, PREFIX + "events");

        control.setMethodCallHandler((call, result) -> {
            if ("ready".equals(call.method)) {
                ready = true;
                result.success(true);
                return;
            }
            if (!"openRoute".equals(call.method)) {
                result.notImplemented();
                return;
            }
            finish(perform(() -> {
                if (!active) throw new BridgeException("inactive", "The host is not in the foreground");
                return CompletableFuture.completedFuture(openRoute.apply(BridgeMethod.string(call.arguments)));
            }), result);
        });
        rpc.setMethodCallHandler((call, result) ->
                finish(perform(() -> registry.dispatch(call.method, call.arguments)), result));
        messages.setMessageHandler((message, reply) -> perform(() -> {
            if (config.messages == null) throw new BridgeException("not_implemented", "No message handler");
            return config.messages.apply(BridgeMethod.map(message));
        }).whenComplete((value, failure) -> {
            if (failure == null) {
                reply.reply(Collections.singletonMap("value", value));
            } else {
                BridgeException error = BridgeException.from(failure);
                Map<String, Object> envelope = new HashMap<>();
                envelope.put("error", error.code);
                envelope.put("message", error.getMessage());
                reply.reply(envelope);
            }
        }));
        events.setStreamHandler(new EventChannel.StreamHandler() {
            @Override
            public void onListen(Object args, EventChannel.EventSink listener) {
                if (!ready || closed) {
                    listener.error("not_ready", "The bridge is not connected", null);
                    listener.endOfStream();
                    return;
                }
                if (source == null) {
                    listener.error("not_implemented", "No event source", null);
                    listener.endOfStream();
                    return;
                }
                sink = listener;
                updateEvents();
            }

            @Override
            public void onCancel(Object args) {
                sink = null;
                updateEvents();
            }
        });
    }

    public <A, R> CompletionStage<R> invoke(BridgeMethod<A, R> method, A args) {
        return perform(() -> invokeChannel(rpc, method.name, method.encode(args)))
                .thenApply(method::decode);
    }

    public CompletionStage<Map<String, Object>> send(Map<String, Object> payload) {
        return perform(() -> {
            CompletableFuture<Object> reply = new CompletableFuture<>();
            messages.send(payload, reply::complete);
            return reply;
        }).thenApply(raw -> {
            if (raw == null) throw new BridgeException("not_implemented", "No Dart message handler");
            Map<String, Object> envelope = decodeReply(raw);
            if (envelope.get("error") instanceof String) {
                throw new BridgeException((String) envelope.get("error"), (String) envelope.get("message"));
            }
            return decodeReply(envelope.get("value"));
        });
    }

    private static Map<String, Object> decodeReply(Object value) {
        try {
            return BridgeMethod.map(value);
        } catch (IllegalArgumentException error) {
            throw new BridgeException("invalid_result", error.getMessage());
        }
    }

    CompletionStage<Boolean> back() {
        return perform(() -> invokeChannel(control, "back", null)).thenApply(Boolean.TRUE::equals);
    }

    void setActive(boolean active) {
        this.active = active;
        updateEvents();
    }

    private void updateEvents() {
        if (active && sink != null && !closed) {
            if (unsubscribe == null) {
                EventChannel.EventSink listener = sink;
                unsubscribe = source.subscribe(value -> main.post(() -> {
                    if (active && sink == listener && !closed) listener.success(value);
                }));
            }
        } else if (unsubscribe != null) {
            unsubscribe.run();
            unsubscribe = null;
        }
    }

    private <T> CompletionStage<T> perform(Supplier<CompletionStage<T>> action) {
        CompletableFuture<T> result = new CompletableFuture<>();
        if (closed || !ready) {
            result.completeExceptionally(new BridgeException(closed ? "closed" : "not_ready",
                    "The bridge is not available"));
            return result;
        }
        try {
            return track(action.get());
        } catch (IllegalArgumentException error) {
            result.completeExceptionally(new BridgeException("invalid_argument", error.getMessage()));
        } catch (RuntimeException error) {
            result.completeExceptionally(BridgeException.from(error));
        }
        return result;
    }

    private CompletionStage<Object> invokeChannel(MethodChannel channel, String method, Object args) {
        CompletableFuture<Object> reply = new CompletableFuture<>();
        channel.invokeMethod(method, args, new MethodChannel.Result() {
            @Override public void success(Object value) { reply.complete(value); }
            @Override public void error(String code, String message, Object details) {
                reply.completeExceptionally(new BridgeException(code, message));
            }
            @Override public void notImplemented() {
                error("not_implemented", "Unknown bridge method: " + method, null);
            }
        });
        return reply;
    }

    private <T> CompletionStage<T> track(CompletionStage<T> reply) {
        CompletableFuture<T> result = new CompletableFuture<>();
        Runnable expire = () -> result.completeExceptionally(
                new BridgeException("timeout", "The bridge call timed out"));
        pending.add(result);
        main.postDelayed(expire, TIMEOUT_MS);
        result.whenComplete((value, failure) -> {
            main.removeCallbacks(expire);
            pending.remove(result);
        });
        reply.whenComplete((value, failure) -> {
            Runnable complete = () -> {
                if (failure == null) result.complete(value);
                else result.completeExceptionally(failure);
            };
            if (Looper.myLooper() == main.getLooper()) complete.run();
            else main.post(complete);
        });
        return result;
    }

    private void finish(CompletionStage<?> operation, MethodChannel.Result result) {
        operation.whenComplete((value, failure) -> {
            if (failure == null) {
                result.success(value);
            } else {
                BridgeException error = BridgeException.from(failure);
                if ("not_implemented".equals(error.code)) result.notImplemented();
                else result.error(error.code, error.getMessage(), null);
            }
        });
    }

    void resetForEngineRestart() {
        ready = false;
        sink = null;
        updateEvents();
        rejectPending("restarting", "The Dart engine is restarting");
    }

    private void rejectPending(String code, String message) {
        for (CompletableFuture<?> result : new ArrayList<>(pending)) {
            result.completeExceptionally(new BridgeException(code, message));
        }
    }

    @Override
    public void close() {
        if (closed) return;
        closed = true;
        updateEvents();
        if (sink != null) sink.endOfStream();
        sink = null;
        control.setMethodCallHandler(null);
        rpc.setMethodCallHandler(null);
        messages.setMessageHandler(null);
        events.setStreamHandler(null);
        rejectPending("closed", "The host was destroyed");
    }
}
