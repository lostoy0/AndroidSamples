package com.lostoy.android.samples.device

import android.content.Context
import android.util.Log
import com.lostoy.android.common.deviceid.AdvertisingIdClient
import com.lostoy.android.common.deviceid.AdvertisingIdClientX
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.Callable
import java.util.concurrent.Executors

object GoogleAdvertisingIdSample {
    private const val TAG = "raymond"

    fun init(context: Context) {
        Executors.newSingleThreadExecutor().execute(object : Runnable {
            override fun run() {
                try {
                    val adid = AdvertisingIdClient.getGoogleAdId(context.getApplicationContext())
                    Log.e(TAG, "AdvertisingIdClient adid: " + adid)
                    AdvertisingIdClientX.determineAdvertisingInfo(context)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    fun initByRxJava(context: Context) {
        Single.fromCallable<String>(Callable {
            AdvertisingIdClient.getGoogleAdId(context.getApplicationContext()).toString()
        })
            .subscribeOn(Schedulers.io())
            .subscribe(Consumer { adId: String? ->
                Log.d(TAG, "onNext: " + adId)
                Log.d(TAG, "onCompleted")
            }, Consumer { error: Throwable? -> Log.e(TAG, "onError", error) })
    }
}
