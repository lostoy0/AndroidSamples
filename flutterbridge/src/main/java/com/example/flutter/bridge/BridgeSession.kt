package com.example.flutter.bridge

import android.os.Looper
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.StandardMessageCodec

import android.os.Handler
import io.flutter.plugin.common.BasicMessageChannel
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import java.util.function.BiConsumer
import java.util.function.Consumer
import java.util.function.Function
import java.util.function.Supplier

/** One bridge per engine. Callers use it on the Android main thread.  */
class BridgeSession internal constructor(
    messenger: BinaryMessenger, config: HybridRuntime.Configuration,
    openRoute: Function<String?, String?>
) : AutoCloseable {
    fun interface EventSource {
        fun subscribe(emit: Consumer<Map<String, Any?>>): Runnable
    }

    private val main: Handler = Handler(Looper.getMainLooper())
    private val pending: MutableSet<CompletableFuture<*>> = HashSet<CompletableFuture<*>>()
    private val control: MethodChannel
    private val rpc: MethodChannel
    private val messages: BasicMessageChannel<Any?>
    private val events: EventChannel
    private val source: EventSource?

    private var ready = false
    private var active = false
    private var closed = false
    private var sink: EventChannel.EventSink? = null
    private var unsubscribe: Runnable? = null

    init {
        val registry = BridgeRegistry(config.plugins)
        source = config.events
        control = MethodChannel(messenger, PREFIX + "control")
        rpc = MethodChannel(messenger, PREFIX + "rpc")
        messages = BasicMessageChannel(
            messenger, PREFIX + "message",
            StandardMessageCodec.INSTANCE
        )
        events = EventChannel(messenger, PREFIX + "events")

        control.setMethodCallHandler({ call, result ->
            if ("ready" == call.method) {
                ready = true
                result.success(true)
                return@setMethodCallHandler
            }
            if ("openRoute" != call.method) {
                result.notImplemented()
                return@setMethodCallHandler
            }
            finish(perform<String?>(Supplier {
                if (!active) throw BridgeException("inactive", "The host is not in the foreground")
                CompletableFuture.completedFuture<String?>(
                    openRoute.apply(
                        BridgeMethod.string(
                            call.arguments
                        )
                    )
                )
            }), result)
        })
        rpc.setMethodCallHandler({ call, result ->
            finish(perform<Any?>(Supplier {
                registry.dispatch(
                    call.method,
                    call.arguments
                )
            }), result)
        })
        messages.setMessageHandler({ message, reply ->
            perform<Map<String, Any?>>(Supplier {
                if (config.messages == null) throw BridgeException(
                    "not_implemented",
                    "No message handler"
                )
                config.messages.apply(BridgeMethod.map(message))
            }).whenComplete(BiConsumer { value: Map<String, Any?>, failure: Throwable? ->
                if (failure == null) {
                    reply.reply(mapOf("value" to value))
                } else {
                    val error: BridgeException = BridgeException.from(failure)
                    val envelope = HashMap<String, Any?>()
                    envelope.put("error", error.code)
                    envelope.put("message", error.message)
                    reply.reply(envelope)
                }
            })
        })
        events.setStreamHandler(object : EventChannel.StreamHandler {
            public override fun onListen(args: Any?, listener: EventChannel.EventSink) {
                if (!ready || closed) {
                    listener.error("not_ready", "The bridge is not connected", null)
                    listener.endOfStream()
                    return
                }
                if (source == null) {
                    listener.error("not_implemented", "No event source", null)
                    listener.endOfStream()
                    return
                }
                sink = listener
                updateEvents()
            }

            public override fun onCancel(args: Any?) {
                sink = null
                updateEvents()
            }
        })
    }

    fun <A, R> invoke(method: BridgeMethod<A, R>, args: A): CompletionStage<R> {
        return perform<Any?>(Supplier { invokeChannel(rpc, method.name, method.encode(args)) })
            .thenApply { method.decode(it) }
    }

    fun send(payload: Map<String, Any?>): CompletionStage<Map<String, Any?>> {
        return perform<Any?>(Supplier {
            val reply = CompletableFuture<Any?>()
            messages.send(payload) { value -> reply.complete(value) }
            reply
        }).thenApply<Map<String, Any?>>(Function { raw: Any? ->
            if (raw == null) throw BridgeException("not_implemented", "No Dart message handler")
            val envelope: Map<String, Any?> = decodeReply(raw)
            if (envelope.get("error") is String) {
                throw BridgeException(
                    envelope.get("error") as String,
                    envelope.get("message") as String?
                )
            }
            decodeReply(envelope.get("value"))
        })
    }

    fun back(): CompletionStage<Boolean> {
        return perform<Any?>(Supplier { invokeChannel(control, "back", null) }).thenApply { it == true }
    }

    fun setActive(active: Boolean) {
        this.active = active
        updateEvents()
    }

    private fun updateEvents() {
        if (active && sink != null && !closed) {
            if (unsubscribe == null) {
                val listener = sink ?: return
                unsubscribe = source!!.subscribe(Consumer { value: Map<String, Any?> ->
                    main.post(
                        Runnable {
                            if (active && sink === listener && !closed) listener.success(value)
                        })
                })
            }
        } else if (unsubscribe != null) {
            unsubscribe!!.run()
            unsubscribe = null
        }
    }

    private fun <T> perform(action: Supplier<CompletionStage<T>>): CompletionStage<T> {
        val result = CompletableFuture<T>()
        if (closed || !ready) {
            result.completeExceptionally(
                BridgeException(
                    if (closed) "closed" else "not_ready",
                    "The bridge is not available"
                )
            )
            return result
        }
        try {
            return track(action.get())
        } catch (error: IllegalArgumentException) {
            result.completeExceptionally(BridgeException("invalid_argument", error.message))
        } catch (error: RuntimeException) {
            result.completeExceptionally(BridgeException.from(error))
        }
        return result
    }

    private fun invokeChannel(
        channel: MethodChannel,
        method: String,
        args: Any?
    ): CompletionStage<Any?> {
        val reply = CompletableFuture<Any?>()
        channel.invokeMethod(method, args, object : MethodChannel.Result {
            public override fun success(value: Any?) {
                reply.complete(value)
            }

            public override fun error(code: String, message: String?, details: Any?) {
                reply.completeExceptionally(BridgeException(code, message))
            }

            public override fun notImplemented() {
                error("not_implemented", "Unknown bridge method: " + method, null)
            }
        })
        return reply
    }

    private fun <T> track(reply: CompletionStage<T>): CompletionStage<T> {
        val result = CompletableFuture<T>()
        val expire = Runnable {
            result.completeExceptionally(
                BridgeException("timeout", "The bridge call timed out")
            )
        }
        pending.add(result)
        main.postDelayed(expire, TIMEOUT_MS)
        result.whenComplete(BiConsumer { value: T, failure: Throwable? ->
            main.removeCallbacks(expire)
            pending.remove(result)
        })
        reply.whenComplete(BiConsumer { value: T, failure: Throwable? ->
            val complete = Runnable {
                if (failure == null) result.complete(value)
                else result.completeExceptionally(failure)
            }
            if (Looper.myLooper() == main.getLooper()) complete.run()
            else main.post(complete)
        })
        return result
    }

    private fun finish(operation: CompletionStage<*>, result: MethodChannel.Result) {
        operation.whenComplete { value: Any?, failure: Throwable? ->
            if (failure == null) {
                result.success(value)
            } else {
                val error: BridgeException = BridgeException.from(failure)
                if ("not_implemented" == error.code) result.notImplemented()
                else result.error(error.code, error.message, null)
            }
        }
    }

    fun resetForEngineRestart() {
        ready = false
        sink = null
        updateEvents()
        rejectPending("restarting", "The Dart engine is restarting")
    }

    private fun rejectPending(code: String, message: String?) {
        for (result in ArrayList<CompletableFuture<*>>(pending)) {
            result.completeExceptionally(BridgeException(code, message))
        }
    }

    override fun close() {
        if (closed) return
        closed = true
        updateEvents()
        sink?.endOfStream()
        sink = null
        control.setMethodCallHandler(null)
        rpc.setMethodCallHandler(null)
        messages.setMessageHandler(null)
        events.setStreamHandler(null)
        rejectPending("closed", "The host was destroyed")
    }

    companion object {
        private const val PREFIX = "android_samples/bridge/v1/"
        private const val TIMEOUT_MS: Long = 5000

        private fun decodeReply(value: Any?): Map<String, Any?> {
            try {
                return BridgeMethod.map(value)
            } catch (error: IllegalArgumentException) {
                throw BridgeException("invalid_result", error.message)
            }
        }
    }
}
