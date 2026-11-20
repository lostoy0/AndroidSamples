package com.lostoy.android.common.deviceid;

import android.content.Context;
import android.util.Log;

import androidx.ads.identifier.AdvertisingIdInfo;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class AdvertisingIdClientX {

    private static final String TAG = "raymond";

    public static void determineAdvertisingInfo(Context context) {
        if (androidx.ads.identifier.AdvertisingIdClient.isAdvertisingIdProviderAvailable(context)) {
            ListenableFuture<AdvertisingIdInfo> infoFuture =
                    androidx.ads.identifier.AdvertisingIdClient.getAdvertisingIdInfo(context.getApplicationContext());
            infoFuture.addListener(() -> {
                try {
                    Log.d(TAG, "AdvertisingIdClientX adid: " + infoFuture.get().getId());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    Log.e(TAG, "AdvertisingIdClientX Interrupted while reading Advertising ID.", e);
                } catch (ExecutionException | CancellationException e) {
                    Log.e(TAG, "AdvertisingIdClientX Failed to connect to Advertising ID provider.", e);
                }
            }, Executors.newCachedThreadPool());
        } else {
            // The Advertising ID client library is unavailable. Use a different
            // library to perform any required ads use cases.
            Log.d(TAG, "AdvertisingIdClientX The Advertising ID client library is unavailable");
        }
    }
}
