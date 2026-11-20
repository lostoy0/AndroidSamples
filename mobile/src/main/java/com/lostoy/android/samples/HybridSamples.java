package com.lostoy.android.samples;

import android.app.Activity;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.example.flutter.bridge.BridgeException;
import com.example.flutter.bridge.BridgeMethod;
import com.example.flutter.bridge.BridgePlugin;
import com.example.flutter.bridge.HybridRuntime;
import com.example.flutter.bridge.RouteTable;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

final class HybridSamples {
    static final String TAB = "samples://flutter/tab";
    static final String FLUTTER_DETAIL = "samples://flutter/detail/42?title=Flutter";
    static final String COMMUNICATIONS = "samples://flutter/communications";

    static final BridgeMethod<String, String> FORMAT =
            new BridgeMethod<>("demo.formatText", BridgeMethod::string, BridgeMethod::string);
    private static final BridgeMethod<String, Map<String, Object>> DEVICE_INFO =
            new BridgeMethod<>("device.getInfo", BridgeMethod::string, BridgeMethod::map);
    private static final BridgeMethod<String, String> GREET =
            new BridgeMethod<>("device.greet", BridgeMethod::string, BridgeMethod::string);

    private HybridSamples() {}

    static HybridRuntime createRuntime() {
        RouteTable routes = new RouteTable()
                .flutter("/detail/{id}", request -> request.positiveInt("id"))
                .flutter("/communications", request -> {})
                .nativePage("/demo/{id}", request -> request.positiveInt("id"),
                        (activity, request) -> activity.startActivity(
                                HybridNativeDetailActivity.intent(activity, request)));

        for (SampleActivity.Sample sample : SampleActivity.Sample.values()) {
            routes.nativePage("/samples/" + sample.slug, request -> {},
                    (activity, request) -> activity.startActivity(SampleActivity.createIntent(activity, sample)));
        }
        BridgePlugin device = () -> Arrays.asList(
                DEVICE_INFO.bind(label -> {
                    Map<String, Object> info = new LinkedHashMap<>();
                    info.put("label", label);
                    info.put("model", Build.MODEL);
                    info.put("sdk", Build.VERSION.SDK_INT);
                    return CompletableFuture.completedFuture(info);
                }),
                GREET.bind(name -> CompletableFuture.completedFuture("Android: " + name)));
        HybridRuntime.Configuration config = new HybridRuntime.Configuration(
                Collections.singletonList(device),
                payload -> CompletableFuture.completedFuture(
                        Collections.singletonMap("echo", "Android: " + BridgeMethod.string(payload.get("text")))),
                HybridSamples::startTicks);
        return new HybridRuntime(routes, config);
    }

    private static Runnable startTicks(Consumer<Map<String, Object>> emit) {
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable ticker = new Runnable() {
            private int count;

            @Override
            public void run() {
                emit.accept(Collections.singletonMap("tick", ++count));
                handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(ticker, 1000);
        return () -> handler.removeCallbacks(ticker);
    }

    static void open(Activity activity, String url) {
        try {
            HybridRuntime.from(activity).router().open(activity, url);
        } catch (BridgeException error) {
            Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
