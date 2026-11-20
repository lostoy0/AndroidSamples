package com.lostoy.android.samples;

import android.app.Application;

import com.example.flutter.bridge.HybridRuntime;

public class MyApplication extends Application implements HybridRuntime.Owner {
    private final HybridRuntime runtime = HybridSamples.createRuntime();

    @Override
    public HybridRuntime hybridRuntime() {
        return runtime;
    }
}
