package xyz.thaihuynh.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.text.TextUtils
import android.util.Log

/**
 * @author  hthai
 * @since   3/27/18
 */
class BasicService : Service() {

    override fun onCreate() {
        super.onCreate()
        Log.d("BasicService", "onCreate")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("BasicService", "onStartCommand")
        intent?.let {
            if (TextUtils.equals(it.getStringExtra(COMMAND), STOP)) {
                stopSelf()
            }
        }
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
        const val STOP = "STOP"
    }
}