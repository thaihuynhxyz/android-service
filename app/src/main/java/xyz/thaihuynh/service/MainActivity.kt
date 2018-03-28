package xyz.thaihuynh.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            intent?.getStringExtra(BackgroundService.STATE)?.let { Log.d("MainActivity", "onReceive: $it") }
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
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(mReceiver, IntentFilter(BackgroundService.NOTIFICATION))
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(mReceiver)
    }
}
