package com.siedler.jonah.mobilecomputinghomework.helper.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.siedler.jonah.mobilecomputinghomework.MainActivity
import com.siedler.jonah.mobilecomputinghomework.MyApplication
import com.siedler.jonah.mobilecomputinghomework.R
import com.siedler.jonah.mobilecomputinghomework.ui.home.HomeFragment
import com.siedler.jonah.mobilecomputinghomework.ui.login.LoginActivity
import java.time.Duration
import java.util.concurrent.TimeUnit

const val CHANNEL_ID = "MobileComputingHomeworkNotificationChannel"
const val channelName = "MobileComputing Homework Notifications"
const val channelDescription = "This is the general channel for notifications of the app MobileComputing Homework"

object NotificationHelper {
    var notificationIdCounter: Int = 0
    init {
        // create a notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = channelName
            val descriptionText = channelDescription
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                MyApplication.instance.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun scheduleNotification(inSeconds: Long, notificationTitle: String, notificationContent: String) {
        val workManager = WorkManager.getInstance(MyApplication.instance)

        val workingJobBuilder = OneTimeWorkRequestBuilder<NotificationWorker>()

        val data = Data.Builder()
        data.putString(NOTIFICATION_TITLE_KEY, notificationTitle)
        data.putString(NOTIFICATION_CONTENT_KEY, notificationContent)

        val workingJob = workingJobBuilder.setInitialDelay(inSeconds, TimeUnit.SECONDS)
            .setInputData(data.build())
            .build()
        workManager.beginWith(workingJob).enqueue()
    }

    fun sendNotification(title: String, content: String) {
        val context: Context = MyApplication.instance

        val resultIntent = Intent(context, MainActivity::class.java)
        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(resultIntent)
            getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)
        }

        var builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.uni_oulu_logo_black)
            .setColor(context.getColor(R.color.error))
            .setContentTitle(title)
            .setContentText(content)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(resultPendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(notificationIdCounter++, builder.build())
        }
    }
}