package xyz.thaihuynh.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import java.util.Random


class LocalService : Service() {

    // Binder given to clients
    private val mBinder = LocalBinder()
    // Random number generator
    private val mGenerator = Random()

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    inner class LocalBinder : Binder() {
        internal// Return this instance of LocalService so clients can call public methods
        val service: LocalService
            get() = this@LocalService
    }

    override fun onBind(intent: Intent): IBinder? {
        return mBinder
    }

    /** method for clients  */
    fun getRandomNumber(): Int {
        return mGenerator.nextInt(100)
    }
}