package com.example.flutter.bridge;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.flutter.embedding.android.FlutterFragment;
import io.flutter.embedding.android.RenderMode;
import io.flutter.embedding.android.TransparencyMode;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.platform.PlatformPlugin;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/** A Flutter tab that owns its engine for the lifetime of the Fragment. */
public final class HybridTabFragment extends FlutterFragment {
    private BridgeSession session;

    public static HybridTabFragment create(String rootUrl) {
        return new NewEngineInGroupFragmentBuilder(HybridTabFragment.class, HybridRuntime.GROUP_ID)
                .initialRoute(rootUrl)
                .handleDeeplinking(false)
                .renderMode(RenderMode.texture)
                .transparencyMode(TransparencyMode.opaque)
                .shouldDelayFirstAndroidViewDraw(false)
                .build();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        HybridRuntime.from(context).ensureEngineGroup(context);
        super.onAttach(context);
    }

    @Nullable
    @Override
    public PlatformPlugin providePlatformPlugin(@Nullable Activity activity, @NonNull FlutterEngine engine) {
        if (activity == null) return null;
        return new PlatformPlugin(activity, engine.getPlatformChannel(), this) {
            @Override
            public void updateSystemUiOverlays() {
                // The native home Activity owns the system bar layout for all tabs.
            }
        };
    }

    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine engine) {
        super.configureFlutterEngine(engine);
        session = HybridRuntime.from(requireActivity()).attach(requireActivity(), engine);
    }

    @Override
    public void cleanUpFlutterEngine(@NonNull FlutterEngine engine) {
        HybridRuntime.from(requireActivity()).detach(session);
        session = null;
        super.cleanUpFlutterEngine(engine);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (session != null) session.setActive(true);
    }

    @Override
    public void onPause() {
        if (session != null) session.setActive(false);
        super.onPause();
    }

    public BridgeSession bridge() {
        if (session == null) throw new BridgeException("not_ready", "The Flutter tab is not attached");
        return session;
    }

    public CompletionStage<Boolean> maybePop() {
        if (session == null || !isResumed()) return CompletableFuture.completedFuture(false);
        return session.back();
    }
}
