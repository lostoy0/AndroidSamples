package com.lostoy.android.samples.utils;

import android.content.Context;
import android.util.Log;

import com.bun.miitmdid.core.MdidSdkHelper;
import com.bun.miitmdid.interfaces.IIdentifierListener;
import com.bun.miitmdid.interfaces.IdSupplier;

public class OAIDUtil {

    private static final String TAG = "raymond";


    public static void init(Context context) {

        MdidSdkHelper.InitSdk(context.getApplicationContext(), true, new IIdentifierListener() {
            @Override
            public void OnSupport(boolean b, IdSupplier idSupplier) {
                if (idSupplier == null) {
                    return;
                }

                String oaid = idSupplier.getOAID();
                String vaid = idSupplier.getVAID();
                String aaid = idSupplier.getAAID();

                String idstext = "support: " + (idSupplier.isSupported() ? "true" : "false") + "\n" +
                        "OAID: " + oaid + "\n" +
                        "VAID: " + vaid + "\n" +
                        "AAID: " + aaid + "\n";
                Log.d(TAG, idstext);
            }
        });
    }
}
