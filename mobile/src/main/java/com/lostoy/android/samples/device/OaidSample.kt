package com.lostoy.android.samples.device

import android.content.Context
import android.system.Os
import android.system.OsConstants
import android.util.Log
import com.bun.miitmdid.core.MdidSdkHelper
import com.bun.miitmdid.interfaces.IIdentifierListener
import com.bun.miitmdid.interfaces.IdSupplier

object OaidSample {
    private const val TAG = "raymond"


    fun init(context: Context) {
        // The bundled OAID SDK can crash in its native initializer on 16 KB devices.
        if (Os.sysconf(OsConstants._SC_PAGESIZE) > 4096) {
            Log.i(TAG, "Skipping legacy OAID SDK on a device with larger memory pages")
            return
        }

        MdidSdkHelper.InitSdk(context.getApplicationContext(), true, object : IIdentifierListener {
            override fun OnSupport(b: Boolean, idSupplier: IdSupplier?) {
                if (idSupplier == null) {
                    return
                }

                val oaid = idSupplier.getOAID()
                val vaid = idSupplier.getVAID()
                val aaid = idSupplier.getAAID()

                val idstext =
                    "support: " + (if (idSupplier.isSupported()) "true" else "false") + "\n" +
                            "OAID: " + oaid + "\n" +
                            "VAID: " + vaid + "\n" +
                            "AAID: " + aaid + "\n"
                Log.d(TAG, idstext)
            }
        })
    }
}
