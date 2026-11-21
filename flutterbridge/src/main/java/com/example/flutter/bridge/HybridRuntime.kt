package com.example.flutter.bridge

import android.app.Activity
import io.flutter.embedding.engine.FlutterEngineGroup
import io.flutter.embedding.engine.FlutterEngineGroupCache

import android.content.Context
import android.util.Log
import io.flutter.embedding.engine.FlutterEngine
import java.util.Collections
import java.util.concurrent.CompletionStage
import java.util.function.Function

/** Owns the process engine group and the configuration shared by all containers.  */
class HybridRuntime(routes: RouteTable, private val config: Configuration) {
    interface Owner {
        fun hybridRuntime(): HybridRuntime
    }

    class Configuration(
        plugins: List<BridgePlugin>,
        val messages: Function<Map<String, Any?>, CompletionStage<Map<String, Any?>>>?,
        events: BridgeSession.EventSource?
    ) {
        val plugins: List<BridgePlugin>
        val events: BridgeSession.EventSource?

        init {
            this.plugins =
                Collections.unmodifiableList(ArrayList(plugins))
            this.events = events
        }
    }

    private val router: HybridRouter
    private var engines = 0
    private var sessions = 0

    init {
        router = HybridRouter(routes)
    }

    fun router(): HybridRouter {
        return router
    }

    fun ensureEngineGroup(context: Context) {
        val cache: FlutterEngineGroupCache = FlutterEngineGroupCache.getInstance()
        if (cache.get(GROUP_ID) == null) {
            cache.put(GROUP_ID, FlutterEngineGroup(context.getApplicationContext()))
        }
    }

    fun attach(host: Activity, engine: FlutterEngine): BridgeSession {
        val session = BridgeSession(
            engine.getDartExecutor().getBinaryMessenger(),
            config, Function { url: String? -> router.openFromFlutter(host, url) })
        engines++
        sessions++
        log("attached")
        engine.addEngineLifecycleListener(object : FlutterEngine.EngineLifecycleListener {
            public override fun onPreEngineRestart() {
                session.resetForEngineRestart()
            }

            public override fun onEngineWillDestroy() {
                engines--
                log("destroyed")
            }
        })
        return session
    }

    fun detach(session: BridgeSession?) {
        if (session == null) return
        session.close()
        sessions--
        log("detached")
    }

    private fun log(action: String?) {
        Log.d(LOG_TAG, action + " engines=" + engines + " sessions=" + sessions)
    }

    companion object {
        const val GROUP_ID: String = "android_samples"
        private const val LOG_TAG = "HybridRuntime"

        fun from(context: Context): HybridRuntime {
            return (context.getApplicationContext() as Owner).hybridRuntime()
        }
    }
}
