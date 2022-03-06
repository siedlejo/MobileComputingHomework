package com.siedler.jonah.mobilecomputinghomework.helper.locations

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.siedler.jonah.mobilecomputinghomework.R


const val DUMMY_NOTIFICATION_CHANNEL_ID = "MobileComputingHomeworkNotificationChannel"

class LocationService: Service() {
    private var serviceStarted = false

    override fun onCreate() {
        super.onCreate()

        serviceStarted = true
        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, DUMMY_NOTIFICATION_CHANNEL_ID)
                .setOngoing(false)
                .setSmallIcon(R.drawable.ic_launcher_background)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager: NotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val notificationChannel = NotificationChannel(DUMMY_NOTIFICATION_CHANNEL_ID, DUMMY_NOTIFICATION_CHANNEL_ID, NotificationManager.IMPORTANCE_LOW)
            notificationChannel.description = DUMMY_NOTIFICATION_CHANNEL_ID
            notificationChannel.setSound(null, null)
            notificationManager.createNotificationChannel(notificationChannel)
            startForeground(1, builder.build())
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        LocationHelper.startLocationUpdates(this)
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}