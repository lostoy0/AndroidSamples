package com.lostoy.android.common.deviceid

import android.content.Context
import android.util.Log
import androidx.ads.identifier.AdvertisingIdClient
import java.util.concurrent.CancellationException
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors

object AdvertisingIdClientX {
    private const val TAG = "raymond"

    @JvmStatic
    fun determineAdvertisingInfo(context: Context) {
        if (AdvertisingIdClient.isAdvertisingIdProviderAvailable(context)) {
            val infoFuture =
                AdvertisingIdClient.getAdvertisingIdInfo(context.getApplicationContext())
            infoFuture.addListener(Runnable {
                try {
                    Log.d(TAG, "AdvertisingIdClientX adid: " + infoFuture.get()!!.getId())
                } catch (e: InterruptedException) {
                    Thread.currentThread().interrupt()
                    Log.e(TAG, "AdvertisingIdClientX Interrupted while reading Advertising ID.", e)
                } catch (e: ExecutionException) {
                    Log.e(
                        TAG,
                        "AdvertisingIdClientX Failed to connect to Advertising ID provider.",
                        e
                    )
                } catch (e: CancellationException) {
                    Log.e(
                        TAG,
                        "AdvertisingIdClientX Failed to connect to Advertising ID provider.",
                        e
                    )
                }
            }, Executors.newCachedThreadPool())
        } else {
            // The Advertising ID client library is unavailable. Use a different
            // library to perform any required ads use cases.
            Log.d(TAG, "AdvertisingIdClientX The Advertising ID client library is unavailable")
        }
    }
}
