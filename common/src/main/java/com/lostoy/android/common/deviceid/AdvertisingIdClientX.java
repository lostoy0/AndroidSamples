package com.lostoy.android.common.deviceid;

import android.content.Context;
import android.util.Log;

import androidx.ads.identifier.AdvertisingIdInfo;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import org.jetbrains.annotations.Nullable;

import java.util.concurrent.Executors;

public class AdvertisingIdClientX {

    private static final String TAG = "raymond";

    public static void determineAdvertisingInfo(Context context) {
        if (androidx.ads.identifier.AdvertisingIdClient.isAdvertisingIdProviderAvailable(context)) {
            ListenableFuture<AdvertisingIdInfo> advertisingIdInfoListenableFuture =
                    androidx.ads.identifier.AdvertisingIdClient.getAdvertisingIdInfo(context.getApplicationContext());
            Futures.addCallback(advertisingIdInfoListenableFuture, new FutureCallback<AdvertisingIdInfo>() {
                @Override
                public void onSuccess(@Nullable AdvertisingIdInfo adInfo) {
                    String id = adInfo.getId();
                    String providerPackageName =
                            adInfo.getProviderPackageName();
                    boolean isLimitTrackingEnabled = adInfo.isLimitAdTrackingEnabled();
                    Log.d(TAG, "AdvertisingIdClientX adid: " + id);
                }

                // Any exceptions thrown by getAdvertisingIdInfo()
                // cause this method to get called.
                @Override
                public void onFailure(Throwable t) {
                    Log.e(TAG, "AdvertisingIdClientX Failed to connect to Advertising ID provider.");
                    // Try to connect to the Advertising ID provider again,
                    // or fall back to an ads solution that doesn't require
                    // using the Advertising ID library.
                }
            }, Executors.newCachedThreadPool());
        } else {
            // The Advertising ID client library is unavailable. Use a different
            // library to perform any required ads use cases.
            Log.d(TAG, "AdvertisingIdClientX The Advertising ID client library is unavailable");
        }
    }
}
