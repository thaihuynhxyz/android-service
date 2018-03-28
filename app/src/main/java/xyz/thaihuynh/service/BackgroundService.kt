package xyz.thaihuynh.service

import android.app.IntentService
import android.content.Intent
import android.util.Log
import java.util.concurrent.TimeUnit


class BackgroundService : IntentService("BackgroundService") {

    /**
     * handle intent on background one by one
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
