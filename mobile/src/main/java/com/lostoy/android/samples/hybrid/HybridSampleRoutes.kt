package com.lostoy.android.samples.hybrid

import android.app.Activity
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.example.flutter.bridge.BridgeException
import com.example.flutter.bridge.BridgeMethod
import com.example.flutter.bridge.BridgePlugin
import com.example.flutter.bridge.HybridRuntime
import com.example.flutter.bridge.RouteTable
import com.lostoy.android.samples.catalog.Sample
import com.lostoy.android.samples.catalog.SampleDetailActivity
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer

internal object HybridSampleRoutes {
    const val TAB = "samples://flutter/tab"
    const val FLUTTER_DETAIL = "samples://flutter/detail/42?title=Flutter"
    const val COMMUNICATIONS = "samples://flutter/communications"

    val FORMAT = BridgeMethod("demo.formatText", BridgeMethod.Companion::string, BridgeMethod.Companion::string)
    private val deviceInfo = BridgeMethod("device.getInfo", BridgeMethod.Companion::string, BridgeMethod.Companion::map)
    private val greet = BridgeMethod("device.greet", BridgeMethod.Companion::string, BridgeMethod.Companion::string)

    fun createRuntime(): HybridRuntime {
        val routes = RouteTable()
            .flutter("/detail/{id}") { it.positiveInt("id") }
            .flutter("/communications") {}
            .nativePage("/demo/{id}", { it.positiveInt("id") }) { activity, route ->
                activity.startActivity(HybridNativeDetailActivity.intent(activity, route))
            }
        for (sample in Sample.entries) {
            routes.nativePage("/samples/${sample.slug}", {}) { activity, _ ->
                activity.startActivity(SampleDetailActivity.createIntent(activity, sample))
            }
        }
        val device = BridgePlugin {
            listOf(
                deviceInfo.bind { label ->
                    CompletableFuture.completedFuture(
                        mapOf("label" to label, "model" to Build.MODEL, "sdk" to Build.VERSION.SDK_INT)
                    )
                },
                greet.bind { name -> CompletableFuture.completedFuture("Android: $name") }
            )
        }
        return HybridRuntime(
            routes,
            HybridRuntime.Configuration(
                listOf(device),
                { payload ->
                    CompletableFuture.completedFuture(
                        mapOf("echo" to "Android: ${BridgeMethod.string(payload["text"])}")
                    )
                },
                ::startTicks
            )
        )
    }

    private fun startTicks(emit: Consumer<Map<String, Any?>>): Runnable {
        val handler = Handler(Looper.getMainLooper())
        val ticker = object : Runnable {
            private var count = 0

            override fun run() {
                emit.accept(mapOf("tick" to ++count))
                handler.postDelayed(this, 1000)
            }
        }
        handler.postDelayed(ticker, 1000)
        return Runnable { handler.removeCallbacks(ticker) }
    }

    fun open(activity: Activity, url: String) {
        try {
            HybridRuntime.from(activity).router().open(activity, url)
        } catch (error: BridgeException) {
            Toast.makeText(activity, error.message, Toast.LENGTH_SHORT).show()
        }
    }
}
