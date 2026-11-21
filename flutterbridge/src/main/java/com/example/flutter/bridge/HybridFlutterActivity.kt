package com.example.flutter.bridge

import android.content.Intent
import android.os.Bundle
import io.flutter.embedding.engine.FlutterEngine

import io.flutter.embedding.android.FlutterActivity

class HybridFlutterActivity : FlutterActivity() {
    private var session: BridgeSession? = null

    protected override fun onCreate(savedInstanceState: Bundle?) {
        HybridRuntime.from(this).ensureEngineGroup(this)
        super.onCreate(savedInstanceState)
    }

    public override fun configureFlutterEngine(engine: FlutterEngine) {
        super.configureFlutterEngine(engine)
        session = HybridRuntime.from(this).attach(this, engine)
    }

    public override fun cleanUpFlutterEngine(engine: FlutterEngine) {
        HybridRuntime.from(this).detach(session)
        session = null
        super.cleanUpFlutterEngine(engine)
    }

    protected override fun onResume() {
        super.onResume()
        if (session != null) session!!.setActive(true)
    }

    protected override fun onPause() {
        if (session != null) session!!.setActive(false)
        super.onPause()
    }

    companion object {
        fun intent(context: android.content.Context, route: String): Intent {
            return NewEngineInGroupIntentBuilder(
                HybridFlutterActivity::class.java,
                HybridRuntime.GROUP_ID
            )
                .initialRoute(route)
                .build(context)
        }
    }
}
