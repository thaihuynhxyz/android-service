package xyz.thaihuynh.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.RemoteCallbackList
import android.os.RemoteException
import android.util.Log

class RemoteService : Service() {

    /**
     * This is a list of callbacks that have been registered with the
     * service.  Note that this is package scoped (instead of private) so
     * that it can be accessed more efficiently from inner classes.
     */
    val mCallbacks = RemoteCallbackList<IRemoteServiceCallback>()

    override fun onCreate() {
        super.onCreate()
        Log.d("RemoteService", "onCreate")
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d("RemoteService", "onStartCommand: startId = $startId, intent = $intent")
        return Service.START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("RemoteService", "onDestroy")
    }

    override fun onBind(intent: Intent): IBinder? {
        // Return the interface
        return mBinder
    }

    /**
     * The IRemoteInterface is defined through IDL
     */
    private val mBinder = object : IRemoteService.Stub() {
        override fun registerCallback(cb: IRemoteServiceCallback?) {
            if (cb != null) mCallbacks.register(cb)
        }

        override fun unregisterCallback(cb: IRemoteServiceCallback?) {
            if (cb != null) mCallbacks.unregister(cb)
        }

        override fun setValue(value: Int) {
            // Broadcast to all clients the new value.
            val n = mCallbacks.beginBroadcast()
            for (i in 0 until n) {
                try {
                    mCallbacks.getBroadcastItem(i).valueChanged(value)
                } catch (e: RemoteException) {
                    // The RemoteCallbackList will take care of removing
                    // the dead object for us.
                }
            }
            mCallbacks.finishBroadcast()
        }
    }
}
