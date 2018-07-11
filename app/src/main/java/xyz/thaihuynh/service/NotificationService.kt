package xyz.thaihuynh.service

import android.arch.lifecycle.ProcessLifecycleOwner
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

class NotificationService : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        Log.e(TAG, "onNotificationPosted=========================================")
        Log.i(TAG, "onNotificationPosted=${ProcessLifecycleOwner.get().lifecycle.currentState}")
        logSbn(sbn)
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        Log.e(TAG, "onNotificationRemoved=========================================")
    }

    private fun logSbn(sbn: StatusBarNotification) {
        Log.e(TAG, "getGroupKey=" + sbn.groupKey)
        Log.e(TAG, "getKey=" + sbn.key)
        Log.e(TAG, "getPackageName=" + sbn.packageName)
        Log.e(TAG, "getTag=" + sbn.tag)
        Log.e(TAG, "getId=" + sbn.id)
        Log.e(TAG, "getPostTime=" + sbn.postTime)
        Log.e(TAG, "getNotification.getGroup=" + sbn.notification.group)
        Log.e(TAG, "getNotification.getSortKey=" + sbn.notification.sortKey)
        Log.e(TAG, "getNotification.category=" + sbn.notification.category)
        Log.e(TAG, "getNotification.number=" + sbn.notification.number)
        Log.e(TAG, "getNotification.flags=" + sbn.notification.flags)
        Log.e(TAG, "getNotification.tickerText=" + sbn.notification.tickerText)
        Log.e(TAG, "getNotification.when=" + sbn.notification.`when`)
        Log.e(TAG, "getNotification.toString=" + sbn.notification.toString())
        for (key in sbn.notification.extras.keySet()) {
            Log.e(TAG, "getNotification.extras: key=$key, value=${sbn.notification.extras.get(key)}")
        }
    }

    companion object {
        private const val TAG = "NotificationService"
    }
}
