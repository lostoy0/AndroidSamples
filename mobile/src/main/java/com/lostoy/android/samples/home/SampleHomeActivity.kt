package com.lostoy.android.samples.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.RadioGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import com.example.flutter.bridge.BridgeException
import com.example.flutter.bridge.HybridTabFragment
import com.lostoy.android.samples.R
import com.lostoy.android.samples.hybrid.HybridSampleRoutes

class SampleHomeActivity : AppCompatActivity() {
    private lateinit var tabs: RadioGroup
    private lateinit var samples: ListView
    private lateinit var flutterPanel: View
    private var selectedTab = HomeTab.WIDGETS
    private var flutterTab: HybridTabFragment? = null
    private var awaitingBack = false
    private var resumed = false

    private val flutterBack = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            val fragment = flutterTab ?: return
            if (awaitingBack) return
            awaitingBack = true
            fragment.maybePop().whenComplete { popped, _ ->
                awaitingBack = false
                if (resumed && selectedTab == HomeTab.FLUTTER && popped != true) {
                    tabs.check(HomeTab.WIDGETS.id)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitle(R.string.sample_catalog_title)
        setContentView(R.layout.activity_main)
        tabs = findViewById(R.id.home_tabs)
        samples = findViewById(R.id.sample_list)
        flutterPanel = findViewById(R.id.flutter_tab_panel)
        flutterTab = supportFragmentManager.findFragmentByTag(FLUTTER_TAG) as? HybridTabFragment

        onBackPressedDispatcher.addCallback(this, flutterBack)
        tabs.setOnCheckedChangeListener { _, id ->
            HomeTab.entries.firstOrNull { it.id == id }?.let(::selectTab)
        }
        val restoredId = savedInstanceState?.getInt(SELECTED_TAB, HomeTab.WIDGETS.id)
        tabs.check(HomeTab.entries.firstOrNull { it.id == restoredId }?.id ?: HomeTab.WIDGETS.id)
        findViewById<View>(R.id.hybrid_open_detail).setOnClickListener {
            HybridSampleRoutes.open(this, HybridSampleRoutes.FLUTTER_DETAIL)
        }
        findViewById<View>(R.id.hybrid_open_communications).setOnClickListener {
            HybridSampleRoutes.open(this, HybridSampleRoutes.COMMUNICATIONS)
        }
        findViewById<View>(R.id.hybrid_call_dart).setOnClickListener { callDart() }
    }

    private fun selectTab(tab: HomeTab) {
        val manager = supportFragmentManager
        if (manager.isStateSaved) return
        selectedTab = tab
        val showFlutter = tab == HomeTab.FLUTTER
        samples.isVisible = !showFlutter
        flutterPanel.isVisible = showFlutter
        if (!showFlutter) {
            samples.adapter = ArrayAdapter(
                this, android.R.layout.simple_list_item_1, tab.samples.map { getString(it.titleRes) }
            )
            samples.setOnItemClickListener { _, _, position, _ ->
                HybridSampleRoutes.open(this, "samples://native/samples/${tab.samples[position].slug}")
            }
        }

        if (showFlutter || flutterTab != null) {
            val transaction = manager.beginTransaction().setReorderingAllowed(true)
            val fragment = flutterTab ?: HybridTabFragment.create(HybridSampleRoutes.TAB).also {
                flutterTab = it
                transaction.add(R.id.flutter_tab_container, it, FLUTTER_TAG)
            }
            if (showFlutter) transaction.show(fragment) else transaction.hide(fragment)
            transaction.setMaxLifecycle(
                fragment, if (showFlutter) Lifecycle.State.RESUMED else Lifecycle.State.STARTED
            )
            transaction.commitNow()
            if (showFlutter && resumed) fragment.onPostResume()
        }
        flutterBack.isEnabled = showFlutter
    }

    private fun callDart() {
        val fragment = flutterTab ?: return
        val result = findViewById<TextView>(R.id.hybrid_dart_result)
        result.isVisible = true
        try {
            val bridge = fragment.bridge()
            bridge.invoke(HybridSampleRoutes.FORMAT, "Android")
                .thenCombine(bridge.send(mapOf("text" to "Android"))) { reply, message ->
                    getString(R.string.hybrid_dart_result, reply, message?.get("echo"))
                }
                .whenComplete { value, error ->
                    if (!isDestroyed) result.text = error?.let(::errorText) ?: value
                }
        } catch (error: BridgeException) {
            result.text = errorText(error)
        }
    }

    override fun onPostResume() {
        super.onPostResume()
        resumed = true
        if (selectedTab == HomeTab.FLUTTER) flutterTab?.onPostResume()
    }

    override fun onPause() {
        resumed = false
        super.onPause()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        flutterTab?.onNewIntent(intent)
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        if (selectedTab == HomeTab.FLUTTER) flutterTab?.onUserLeaveHint()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        flutterTab?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        flutterTab?.onActivityResult(requestCode, resultCode, data)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(SELECTED_TAB, selectedTab.id)
        super.onSaveInstanceState(outState)
    }

    companion object {
        private const val FLUTTER_TAG = "flutter-tab"
        private const val SELECTED_TAB = "selected-tab"

        private fun errorText(error: Throwable): String? = (error.cause ?: error).message
    }
}
