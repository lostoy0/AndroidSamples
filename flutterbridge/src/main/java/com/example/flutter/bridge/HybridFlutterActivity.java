package com.example.flutter.bridge;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;

public final class HybridFlutterActivity extends FlutterActivity {
    private BridgeSession session;

    static Intent intent(Context context, String route) {
        return new NewEngineInGroupIntentBuilder(HybridFlutterActivity.class, HybridRuntime.GROUP_ID)
                .initialRoute(route)
                .build(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        HybridRuntime.from(this).ensureEngineGroup(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine engine) {
        super.configureFlutterEngine(engine);
        session = HybridRuntime.from(this).attach(this, engine);
    }

    @Override
    public void cleanUpFlutterEngine(@NonNull FlutterEngine engine) {
        HybridRuntime.from(this).detach(session);
        session = null;
        super.cleanUpFlutterEngine(engine);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (session != null) session.setActive(true);
    }

    @Override
    protected void onPause() {
        if (session != null) session.setActive(false);
        super.onPause();
    }
}
