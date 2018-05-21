package xyz.thaihuynh.service

import android.app.IntentService
import android.content.Intent
import android.util.Log
import java.util.concurrent.TimeUnit

/**
 * A constructor is required, and must call the super IntentService(String)
 * constructor with a name for the worker thread.
 */
class BackgroundService : IntentService("BackgroundService") {

    /**
     * The IntentService calls this method from the default worker thread with
     * the intent that started the service. When this method returns, IntentService
     * stops the service, as appropriate.
     */
    override fun onHandleIntent(intent: Intent?) {
        Log.d("BackgroundService", "onHandleIntent run on ${Thread.currentThread().name}")
        sendBroadcast(Intent(NOTIFICATION).also { it.putExtra(STATE, START) })
        Thread.sleep(TimeUnit.SECONDS.toMillis(2))
        sendBroadcast(Intent(NOTIFICATION).also { it.putExtra(STATE, END) })
    }

    companion object {
        const val NOTIFICATION = "NOTIFICATION"
        const val STATE = "STATE"
        const val START = "START"
        const val END = "END"
    }
}
