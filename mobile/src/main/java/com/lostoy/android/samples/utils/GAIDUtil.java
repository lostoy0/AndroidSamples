package com.lostoy.android.samples.utils;

import android.content.Context;
import android.util.Log;

import com.lostoy.android.common.deviceid.AdvertisingIdClient;
import com.lostoy.android.common.deviceid.AdvertisingIdClientX;

import java.util.concurrent.Executors;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

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
        Observable.create((Observable.OnSubscribe<String>) subscriber -> {
            subscriber.onNext(AdvertisingIdClient.getGoogleAdId(context.getApplicationContext()));
            subscriber.onCompleted();
        }).subscribeOn(Schedulers.io()).subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {
                Log.d(TAG, "onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                Log.d(TAG, "onError");
            }

            @Override
            public void onNext(String s) {
                Log.d(TAG, "onNext: " + s);
            }
        });
    }
}
