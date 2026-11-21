package com.example.flutter.bridge

import android.app.Activity
import com.example.flutter.bridge.RouteTable.UnknownRoute

class HybridRouter internal constructor(private val routes: RouteTable) {
    fun open(activity: Activity, url: String?) {
        open(activity, url, false)
    }

    fun openFromFlutter(activity: Activity, url: String?): String? {
        return open(activity, url, true)
    }

    private fun open(activity: Activity, url: String?, fromFlutter: Boolean): String? {
        if (activity.isFinishing() || activity.isDestroyed()) {
            throw BridgeException("inactive", "The host is leaving")
        }
        val match: RouteTable.Match
        try {
            match = routes.resolve(url)
        } catch (error: UnknownRoute) {
            throw BridgeException("unknown_route", error.message)
        } catch (error: IllegalArgumentException) {
            throw BridgeException("invalid_route", error.message)
        }
        if (match.nativeDestination != null) {
            match.nativeDestination.open(activity, match.request)
        } else if (fromFlutter) {
            return match.request.url
        } else {
            activity.startActivity(
                HybridFlutterActivity.intent(
                    activity,
                    match.request.url
                )
            )
        }
        return ""
    }
}
