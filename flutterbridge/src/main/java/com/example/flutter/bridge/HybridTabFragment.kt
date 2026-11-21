package com.example.flutter.bridge

import android.app.Activity
import io.flutter.embedding.android.RenderMode
import io.flutter.embedding.android.TransparencyMode
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.platform.PlatformPlugin

import android.content.Context
import io.flutter.embedding.android.FlutterFragment
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage

/** A Flutter tab that owns its engine for the lifetime of the Fragment.  */
class HybridTabFragment : FlutterFragment() {
    private var session: BridgeSession? = null

    public override fun onAttach(context: Context) {
        HybridRuntime.from(context).ensureEngineGroup(context)
        super.onAttach(context)
    }

    public override fun providePlatformPlugin(
        activity: Activity?,
        engine: FlutterEngine
    ): PlatformPlugin? {
        if (activity == null) return null
        return object : PlatformPlugin(activity, engine.getPlatformChannel(), this) {
            public override fun updateSystemUiOverlays() {
                // The native home Activity owns the system bar layout for all tabs.
            }
        }
    }

    public override fun configureFlutterEngine(engine: FlutterEngine) {
        super.configureFlutterEngine(engine)
        session = HybridRuntime.from(requireActivity()).attach(requireActivity(), engine)
    }

    public override fun cleanUpFlutterEngine(engine: FlutterEngine) {
        HybridRuntime.from(requireActivity()).detach(session)
        session = null
        super.cleanUpFlutterEngine(engine)
    }

    public override fun onResume() {
        super.onResume()
        if (session != null) session!!.setActive(true)
    }

    public override fun onPause() {
        if (session != null) session!!.setActive(false)
        super.onPause()
    }

    fun bridge(): BridgeSession {
        if (session == null) throw BridgeException("not_ready", "The Flutter tab is not attached")
        return session!!
    }

    fun maybePop(): CompletionStage<Boolean> {
        if (session == null || !isResumed()) return CompletableFuture.completedFuture(
            false
        )
        return session!!.back()
    }

    companion object {
        fun create(rootUrl: String): HybridTabFragment {
            return NewEngineInGroupFragmentBuilder(
                HybridTabFragment::class.java,
                HybridRuntime.GROUP_ID
            )
                .initialRoute(rootUrl)
                .handleDeeplinking(false)
                .renderMode(RenderMode.texture)
                .transparencyMode(TransparencyMode.opaque)
                .shouldDelayFirstAndroidViewDraw(false)
                .build()
        }
    }
}
