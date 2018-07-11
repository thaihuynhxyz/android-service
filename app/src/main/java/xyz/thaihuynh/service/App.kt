package xyz.thaihuynh.service

import android.app.Application
import android.util.Log

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Log.i("", "onCreate")
    }

    override fun onTerminate() {
        super.onTerminate()
        Log.i("", "onTerminate")
    }
}