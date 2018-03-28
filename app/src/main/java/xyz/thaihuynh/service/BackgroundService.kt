package xyz.thaihuynh.service

import android.app.IntentService
import android.content.Intent
import android.util.Log

class BackgroundService : IntentService("BackgroundService") {

    /**
     * handle intent on background
     */
    override fun onHandleIntent(intent: Intent?) {
        Log.d("BackgroundService", "onHandleIntent run on ${Thread.currentThread().name}")
        intent?.getStringExtra(DATA)?.let { Log.d("BackgroundService", "onHandleIntent: $it") }
    }

    companion object {
        const val DATA = "DATA"
        const val STRING = "STRING"
    }
}