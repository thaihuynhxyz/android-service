package xyz.thaihuynh.service

import android.app.AlertDialog
import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import android.os.RemoteException
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import xyz.thaihuynh.service.LocalService.LocalBinder
import java.io.File

class MainActivity : AppCompatActivity() {

    var mLocalService: LocalService? = null

    /** Messenger for communicating with the service.  */
    var mMessengerService: Messenger? = null

    /** The primary interface we will be calling on the service.  */
    var mRemoteService: IRemoteService? = null

    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            intent?.getStringExtra(BackgroundService.STATE)?.let { Log.d("MainActivity", "onReceive: $it") }
        }
    }

    /**
     * This implementation is used to receive callbacks from the remote
     * service.
     */
    private val mCallback = object : IRemoteServiceCallback.Stub() {

        override fun onLoadFileSuccess() {
            Log.d("MainActivity", "onLoadFileSuccess")
        }

        override fun onLoadFileFailed() {
            Log.d("MainActivity", "onLoadFileFailed")
        }
    }

    /** Defines callbacks for service binding, passed to bindService()  */
    private val mLocalConnection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName,
                                        service: IBinder) {
            Log.d("MainActivity", "mLocalConnection onServiceConnected")
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            mLocalService = (service as LocalBinder).service
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            Log.d("MainActivity", "onServiceDisconnected")
            mLocalService = null
        }
    }

    /**
     * Class for interacting with the main interface of the service.
     */
    private val mMessengerConnection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName,
                                        service: IBinder) {
            Log.d("MainActivity", "mMessengerConnection onServiceConnected")
            // This is called when the connection with the service has been
            // established, giving us the object we can use to
            // interact with the service.  We are communicating with the
            // service using a Messenger, so here we get a client-side
            // representation of that from the raw IBinder object.
            mMessengerService = Messenger(service)
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            Log.d("MainActivity", "mMessengerConnection onServiceDisconnected")
            mMessengerService = null
        }
    }

    /**
     * Class for interacting with the main interface of the service.
     */
    private val mRemoteConnection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  We are communicating with our
            // service through an IDL interface, so get a client-side
            // representation of that from the raw service object.
            mRemoteService = IRemoteService.Stub.asInterface(service)

            // We want to monitor the service for as long as we are
            // connected to it.
            try {
                mRemoteService?.registerCallback(mCallback)
            } catch (e: RemoteException) {
                // In this case the service has crashed before we could even
                // do anything with it; we can count on soon being
                // disconnected (and then reconnected if it can be restarted)
                // so there is no need to do anything here.
            }

            // As part of the sample, tell the user what happened.
            Log.d("MainActivity", "mRemoteConnection onServiceConnected")
        }

        override fun onServiceDisconnected(className: ComponentName) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            mRemoteService = null

            // As part of the sample, tell the user what happened.
            Log.d("MainActivity", "mRemoteConnection onServiceDisconnected")
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

        remoteDownload.setOnClickListener {
            Injection.provideFilesRepository(this).getFile(FilesRepository.FILE_URL, object : FilesLocalDataSource.GetFileCallback {

                override fun onFileLoaded(file: File) {
                    Log.d("MainActivity", "onFileLoaded")
                }

                override fun onDataNotAvailable() {
                    Log.d("MainActivity", "onDataNotAvailable")
                }
            })
        }

        remoteLoad.setOnClickListener {
            mRemoteService?.loadFile(FilesRepository.FILE_URL)
        }
    }

    override fun onResume() {
        super.onResume()

        if (!isNotificationServiceEnabled()) {
            AlertDialog.Builder(this)
                    .setTitle(R.string.notification_listener_service)
                    .setMessage(R.string.notification_listener_service_explanation)
                    .setPositiveButton(R.string.yes) { _, _ -> startActivity(Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS)) }
                    .setNegativeButton(R.string.no) { _, _ ->
                        // If you choose to not enable the notification listener
                        // the app. will not work as expected
                        finish()
                    }
                    .show()
        }

        registerReceiver(mReceiver, IntentFilter(BackgroundService.NOTIFICATION))

        // Bind to LocalService
        bindService(Intent(this, LocalService::class.java), mLocalConnection, Context.BIND_AUTO_CREATE)

        bindService(Intent(this, MessengerService::class.java), mMessengerConnection, Context.BIND_AUTO_CREATE)
        bindService(Intent(this, RemoteService::class.java), mRemoteConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(mReceiver)

        unbindService(mLocalConnection)
        unbindService(mMessengerConnection)
        unbindService(mRemoteConnection)
    }

    /**
     * Is Notification Service Enabled.
     * Verifies if the notification listener service is enabled.
     * Got it from: https://github.com/kpbird/NotificationListenerService-Example/blob/master/NLSExample/src/main/java/com/kpbird/nlsexample/NLService.java
     * @return True if eanbled, false otherwise.
     */
    private fun isNotificationServiceEnabled(): Boolean {
        val flat = Settings.Secure.getString(contentResolver, ENABLED_NOTIFICATION_LISTENERS)
        if (!TextUtils.isEmpty(flat)) {
            val names = flat.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (i in names.indices) {
                val cn = ComponentName.unflattenFromString(names[i])
                if (cn != null) {
                    if (TextUtils.equals(packageName, cn.packageName)) {
                        return true
                    }
                }
            }
        }
        return false
    }

    companion object {
        private const val ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners"
        private const val ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"
    }
}
