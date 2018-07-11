package xyz.thaihuynh.service

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log


class NotificationListener : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        Log.i(TAG, "**********  onNotificationPosted")
        Log.i(TAG, "ID :" + sbn.id + "t" + sbn.notification.tickerText + "t" + sbn.packageName)
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        Log.i(TAG, "********** onNotificationRemoved")
        Log.i(TAG, "ID :" + sbn.id + "t" + sbn.notification.tickerText + "t" + sbn.packageName)
    }

    companion object {
        private const val TAG = "NotificationListener"
    }
}
