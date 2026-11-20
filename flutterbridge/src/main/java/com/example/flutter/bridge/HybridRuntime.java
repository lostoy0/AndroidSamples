package com.example.flutter.bridge;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.FlutterEngineGroup;
import io.flutter.embedding.engine.FlutterEngineGroupCache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

/** Owns the process engine group and the configuration shared by all containers. */
public final class HybridRuntime {
    public interface Owner {
        HybridRuntime hybridRuntime();
    }

    public static final class Configuration {
        final List<BridgePlugin> plugins;
        final Function<Map<String, Object>, CompletionStage<Map<String, Object>>> messages;
        final BridgeSession.EventSource events;

        public Configuration(List<BridgePlugin> plugins,
                Function<Map<String, Object>, CompletionStage<Map<String, Object>>> messages,
                BridgeSession.EventSource events) {
            this.plugins = Collections.unmodifiableList(new ArrayList<>(plugins));
            this.messages = messages;
            this.events = events;
        }
    }

    static final String GROUP_ID = "android_samples";
    private static final String LOG_TAG = "HybridRuntime";

    private final Configuration config;
    private final HybridRouter router;
    private int engines;
    private int sessions;

    public HybridRuntime(RouteTable routes, Configuration config) {
        this.config = config;
        router = new HybridRouter(routes);
    }

    public static HybridRuntime from(Context context) {
        return ((Owner) context.getApplicationContext()).hybridRuntime();
    }

    public HybridRouter router() {
        return router;
    }

    void ensureEngineGroup(Context context) {
        FlutterEngineGroupCache cache = FlutterEngineGroupCache.getInstance();
        if (cache.get(GROUP_ID) == null) {
            cache.put(GROUP_ID, new FlutterEngineGroup(context.getApplicationContext()));
        }
    }

    BridgeSession attach(Activity host, FlutterEngine engine) {
        BridgeSession session = new BridgeSession(engine.getDartExecutor().getBinaryMessenger(),
                config, url -> router.openFromFlutter(host, url));
        engines++;
        sessions++;
        log("attached");
        engine.addEngineLifecycleListener(new FlutterEngine.EngineLifecycleListener() {
            @Override public void onPreEngineRestart() {
                session.resetForEngineRestart();
            }
            @Override public void onEngineWillDestroy() {
                engines--;
                log("destroyed");
            }
        });
        return session;
    }

    void detach(BridgeSession session) {
        if (session == null) return;
        session.close();
        sessions--;
        log("detached");
    }

    private void log(String action) {
        Log.d(LOG_TAG, action + " engines=" + engines + " sessions=" + sessions);
    }
}
