package xyz.thaihuynh.service

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.util.Log


class ForegroundService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("ForegroundService", "onStartCommand run on ${Thread.currentThread().name}")
        when (intent?.getStringExtra(COMMAND)) {
            COMMAND_START -> {
                val pendingIntent = PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java), 0)

                val notification = NotificationCompat.Builder(this, DEFAULT_CHANNEL_ID)
                        .setContentTitle("Title")
                        .setContentText("Text")
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setContentIntent(pendingIntent)
                        .setTicker("Ticker")
                        .build()

                startForeground(ONGOING_NOTIFICATION_ID, notification)
            }
            COMMAND_STOP -> stopForeground(true)
        }
        return Service.START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        const val ONGOING_NOTIFICATION_ID = 1
        const val DEFAULT_CHANNEL_ID = "miscellaneous"
        const val COMMAND = "COMMAND"
        const val COMMAND_START = "COMMAND_START"
        const val COMMAND_STOP = "COMMAND_STOP"
    }
}