package com.lostoy.android.samples.utils;

import android.content.Context;
import android.util.Log;

import com.lostoy.android.common.deviceid.AdvertisingIdClient;
import com.lostoy.android.common.deviceid.AdvertisingIdClientX;

import java.util.concurrent.Executors;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class GAIDUtil {

    private static final String TAG = "raymond";

    public static void init(Context context) {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String adid = AdvertisingIdClient.getGoogleAdId(context.getApplicationContext());
                    Log.e(TAG, "AdvertisingIdClient adid: " + adid);
                    AdvertisingIdClientX.determineAdvertisingInfo(context);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void initByRxJava(Context context) {
        Single.fromCallable(() ->
                        String.valueOf(AdvertisingIdClient.getGoogleAdId(context.getApplicationContext())))
                .subscribeOn(Schedulers.io())
                .subscribe(adId -> {
                    Log.d(TAG, "onNext: " + adId);
                    Log.d(TAG, "onCompleted");
                }, error -> Log.e(TAG, "onError", error));
    }
}
