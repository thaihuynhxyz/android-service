package xyz.thaihuynh.service

import android.app.Application
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.arch.lifecycle.ProcessLifecycleOwner
import android.util.Log

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "onCreate")
        ProcessLifecycleOwner.get().lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_START)
            fun onStart() {
                Log.i(TAG, "onStart=${ProcessLifecycleOwner.get().lifecycle.currentState}")
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
            fun onStop() {
                Log.i(TAG, "onStop=${ProcessLifecycleOwner.get().lifecycle.currentState}")
            }
        })
    }

    override fun onTerminate() {
        super.onTerminate()
        Log.i(TAG, "onTerminate")
    }

    companion object {
        private const val TAG = "App"
    }
}