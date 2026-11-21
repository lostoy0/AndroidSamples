package com.lostoy.android.samples.app

import com.example.flutter.bridge.HybridRuntime
import com.lostoy.android.samples.hybrid.HybridSampleRoutes

class SamplesApplication : android.app.Application(), HybridRuntime.Owner {
    private val runtime: HybridRuntime = HybridSampleRoutes.createRuntime()

    public override fun hybridRuntime(): HybridRuntime {
        return runtime
    }
}
