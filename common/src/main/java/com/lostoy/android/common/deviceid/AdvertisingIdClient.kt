package com.lostoy.android.common.deviceid

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.IInterface
import android.os.Parcel
import android.os.RemoteException
import java.util.concurrent.LinkedBlockingQueue

object AdvertisingIdClient {
    /**
     * 这个方法是耗时的，不能在主线程调用
     */
    @JvmStatic
    fun getGoogleAdId(context: Context): String? {
        val pm = context.getPackageManager()
        try {
            pm.getPackageInfo("com.android.vending", 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val connection = AdvertisingConnection()
        val intent = Intent("com.google.android.gms.ads.identifier.service.START")
        intent.setPackage("com.google.android.gms")
        if (context.bindService(intent, connection, Context.BIND_AUTO_CREATE)) {
            try {
                val adInterface = AdvertisingInterface(connection.binder!!)
                return adInterface.id
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                context.unbindService(connection)
            }
        }
        return ""
    }

    private class AdvertisingConnection : ServiceConnection {
        var retrieved: Boolean = false
        private val queue = LinkedBlockingQueue<IBinder?>(1)

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            try {
                this.queue.put(service)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
        }

        @get:Throws(InterruptedException::class)
        val binder: IBinder?
            get() {
                check(!this.retrieved)
                this.retrieved = true
                return this.queue.take()
            }
    }

    private class AdvertisingInterface(private val binder: IBinder) : IInterface {
        override fun asBinder(): IBinder {
            return binder
        }

        @get:Throws(RemoteException::class)
        val id: String?
            get() {
                val data = Parcel.obtain()
                val reply = Parcel.obtain()
                var id: String?
                try {
                    data.writeInterfaceToken("com.google.android.gms.ads.identifier.internal.IAdvertisingIdService")
                    binder.transact(1, data, reply, 0)
                    reply.readException()
                    id = reply.readString()
                } finally {
                    reply.recycle()
                    data.recycle()
                }
                return id
            }

        @Throws(RemoteException::class)
        fun isLimitAdTrackingEnabled(paramBoolean: Boolean): Boolean {
            val data = Parcel.obtain()
            val reply = Parcel.obtain()
            var limitAdTracking: Boolean
            try {
                data.writeInterfaceToken("com.google.android.gms.ads.identifier.internal.IAdvertisingIdService")
                data.writeInt(if (paramBoolean) 1 else 0)
                binder.transact(2, data, reply, 0)
                reply.readException()
                limitAdTracking = 0 != reply.readInt()
            } finally {
                reply.recycle()
                data.recycle()
            }
            return limitAdTracking
        }
    }
}
