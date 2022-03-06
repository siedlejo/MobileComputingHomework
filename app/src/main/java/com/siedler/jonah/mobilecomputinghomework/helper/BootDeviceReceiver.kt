package com.siedler.jonah.mobilecomputinghomework.helper

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.siedler.jonah.mobilecomputinghomework.helper.locations.LocationService

class BootDeviceReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            ContextCompat.startForegroundService(it, Intent(it, LocationService::class.java))
        }
    }
}