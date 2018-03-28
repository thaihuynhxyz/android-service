package xyz.thaihuynh.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class BasicService : Service() {

    override fun onCreate() {
        super.onCreate()
        Log.d("BasicService", "onCreate")
    }

    /**
     * Basic service run command on main thread.
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("BasicService", "onStartCommand run on ${Thread.currentThread().name}")
        intent?.getStringExtra(COMMAND)?.takeIf { it == COMMAND_STOP }?.apply { stopSelf() }
        return Service.START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("BasicService", "onDestroy")
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    companion object {
        const val COMMAND = "COMMAND"
        const val COMMAND_STOP = "COMMAND_STOP"
    }
}
