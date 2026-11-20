package com.example.flutter.bridge;

import android.app.Activity;

public final class HybridRouter {
    private final RouteTable routes;

    HybridRouter(RouteTable routes) {
        this.routes = routes;
    }

    public void open(Activity activity, String url) {
        open(activity, url, false);
    }

    String openFromFlutter(Activity activity, String url) {
        return open(activity, url, true);
    }

    private String open(Activity activity, String url, boolean fromFlutter) {
        if (activity.isFinishing() || activity.isDestroyed()) {
            throw new BridgeException("inactive", "The host is leaving");
        }
        RouteTable.Match match;
        try {
            match = routes.resolve(url);
        } catch (RouteTable.UnknownRoute error) {
            throw new BridgeException("unknown_route", error.getMessage());
        } catch (IllegalArgumentException error) {
            throw new BridgeException("invalid_route", error.getMessage());
        }
        if (match.nativeDestination != null) {
            match.nativeDestination.open(activity, match.request);
        } else if (fromFlutter) {
            return match.request.url;
        } else {
            activity.startActivity(HybridFlutterActivity.intent(activity, match.request.url));
        }
        return "";
    }
}
