package xyz.thaihuynh.service

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import android.os.RemoteException
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import xyz.thaihuynh.service.LocalService.LocalBinder


class MainActivity : AppCompatActivity() {

    var mLocalService: LocalService? = null

    /** Messenger for communicating with the service.  */
    var mMessengerService: Messenger? = null

    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            intent?.getStringExtra(BackgroundService.STATE)?.let { Log.d("MainActivity", "onReceive: $it") }
        }
    }

    /** Defines callbacks for service binding, passed to bindService()  */
    private val mConnection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName,
                                        service: IBinder) {
            Log.d("MainActivity", "onServiceConnected")
            when (service) {
                is LocalBinder -> {
                    // We've bound to LocalService, cast the IBinder and get LocalService instance
                    mLocalService = service.service
                }
                else -> {
                    // This is called when the connection with the service has been
                    // established, giving us the object we can use to
                    // interact with the service.  We are communicating with the
                    // service using a Messenger, so here we get a client-side
                    // representation of that from the raw IBinder object.
                    mMessengerService = Messenger(service)
                }
            }
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            Log.d("MainActivity", "onServiceDisconnected")
            mLocalService = null
            mMessengerService = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        basicStart.setOnClickListener { startService(Intent(this, BasicService::class.java)) }
        basicStop.setOnClickListener { stopService(Intent(this, BasicService::class.java)) }
        basicStopSelf.setOnClickListener { startService(Intent(this, BasicService::class.java).also { it.putExtra(BasicService.COMMAND, BasicService.COMMAND_STOP) }) }

        backgroundCommand.setOnClickListener { startService(Intent(this, BackgroundService::class.java)) }

        foregroundStart.setOnClickListener { startService(Intent(this, ForegroundService::class.java).also { it.putExtra(ForegroundService.COMMAND, ForegroundService.COMMAND_START) }) }
        foregroundStop.setOnClickListener { startService(Intent(this, ForegroundService::class.java).also { it.putExtra(ForegroundService.COMMAND, ForegroundService.COMMAND_STOP) }) }

        localFunction.setOnClickListener { Log.d("MainActivity", "localFunction: ${mLocalService?.getRandomNumber()}") }

        messengerMessage.setOnClickListener {
            // Create and send a message to the service, using a supported 'what' value
            try {
                mMessengerService?.send(Message.obtain(null, MessengerService.MSG_SAY_HELLO, 0, 0))
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(mReceiver, IntentFilter(BackgroundService.NOTIFICATION))

        // Bind to LocalService
        bindService(Intent(this, LocalService::class.java), mConnection, Context.BIND_AUTO_CREATE)

        bindService(Intent(this, MessengerService::class.java), mConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(mReceiver)

        unbindService(mConnection)
    }
}
