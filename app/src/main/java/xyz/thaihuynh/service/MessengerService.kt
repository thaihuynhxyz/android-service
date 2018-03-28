package xyz.thaihuynh.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import android.widget.Toast


class MessengerService : Service() {

    /**
     * Handler of incoming messages from clients.
     */
    @SuppressLint("HandlerLeak")
    internal inner class IncomingHandler : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_SAY_HELLO -> Toast.makeText(applicationContext, "hello!", Toast.LENGTH_SHORT).show()
                else -> super.handleMessage(msg)
            }
        }
    }

    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    private val mMessenger = Messenger(IncomingHandler())

    /**
     * When binding to the service, we return an interface to our messenger
     * for sending messages to the service.
     */
    override fun onBind(intent: Intent): IBinder? {
        Toast.makeText(applicationContext, "binding", Toast.LENGTH_SHORT).show()
        return mMessenger.binder
    }

    companion object {
        /** Command to the service to display a message  */
        const val MSG_SAY_HELLO = 1
    }
}