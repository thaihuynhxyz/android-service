package xyz.thaihuynh.service

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        basicStart.setOnClickListener { startService(Intent(this, BasicService::class.java)) }
        basicStop.setOnClickListener { stopService(Intent(this, BasicService::class.java)) }
        basicStopSelf.setOnClickListener { startService(Intent(this, BasicService::class.java).also { it.putExtra(BasicService.COMMAND, BasicService.COMMAND_STOP) }) }

        backgroundCommand.setOnClickListener { startService(Intent(this, BackgroundService::class.java).also { it.putExtra(BackgroundService.DATA, BackgroundService.STRING) }) }
    }
}
