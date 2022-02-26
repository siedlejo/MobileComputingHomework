package com.siedler.jonah.mobilecomputinghomework.helper.notifications

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.siedler.jonah.mobilecomputinghomework.MainActivity
import com.siedler.jonah.mobilecomputinghomework.MyApplication
import com.siedler.jonah.mobilecomputinghomework.R
import com.siedler.jonah.mobilecomputinghomework.ui.home.HomeFragment
import java.util.concurrent.TimeUnit


const val CHANNEL_ID = "MobileComputingHomeworkNotificationChannel"
const val channelName = "MobileComputing Homework Notifications"
const val channelDescription = "This is the general channel for notifications of the app MobileComputing Homework"

object NotificationHelper {
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

    fun scheduleNotification(inSeconds: Long, notificationId: String, notificationTitle: String, notificationContent: String) {
        if (inSeconds < 0) {
            return
        }

        val workManager = WorkManager.getInstance(MyApplication.instance)

        // cancel all existing notifications for this reminder
        cancelScheduledNotification(notificationId)
        removeNotification(notificationId)

        // start a new working job to schedule the notification
        val workingJobBuilder = OneTimeWorkRequestBuilder<NotificationWorker>()

        val data = Data.Builder()
        data.putString(NOTIFICATION_ID_KEY, notificationId)
        data.putString(NOTIFICATION_TITLE_KEY, notificationTitle)
        data.putString(NOTIFICATION_CONTENT_KEY, notificationContent)

        val workingJob = workingJobBuilder.setInitialDelay(inSeconds, TimeUnit.SECONDS)
            .setInputData(data.build())
            .addTag(notificationId)
            .build()
        workManager.beginWith(workingJob).enqueue()
    }

    fun cancelScheduledNotification(notificationId: String) {
        val workManager = WorkManager.getInstance(MyApplication.instance)
        workManager.cancelAllWorkByTag(notificationId)
    }

    fun cancelAllScheduledNotifications() {
        val workManager = WorkManager.getInstance(MyApplication.instance)
        workManager.cancelAllWork()
    }

    fun removeAllNotifications() {
        val context = MyApplication.instance
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        notificationManager?.cancelAll()
    }

    fun removeNotification(reminderId: String) {
        val context = MyApplication.instance
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        notificationManager?.cancel(reminderId, reminderId.hashCode())
    }

    fun sendNotification(id: String, title: String, content: String) {
        val context: Context = MyApplication.instance

        val resultIntent = Intent(context, MainActivity::class.java)
        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(resultIntent)
            getPendingIntent(id.hashCode(), PendingIntent.FLAG_IMMUTABLE)
        }

        val markAsSeenIntent = Intent(context, NotificationBroadcastReceiver::class.java)
        markAsSeenIntent.action = MARK_AS_SEEN_ACTION
        markAsSeenIntent.putExtra(NOTIFICATION_ID_MARK_AS_SEEN_ACTION, id)
        val markAsSeenAction = PendingIntent.getBroadcast(context, (id + MARK_AS_SEEN_ACTION).hashCode(), markAsSeenIntent, PendingIntent.FLAG_IMMUTABLE)

        val dismissNotificationIntent = Intent(context, NotificationBroadcastReceiver::class.java)
        dismissNotificationIntent.action = DISMISS_ACTION
        dismissNotificationIntent.putExtra(NOTIFICATION_ID_DISMISS_ACTION, id)
        val dismissNotificationAction = PendingIntent.getBroadcast(context, (id + DISMISS_ACTION).hashCode(), dismissNotificationIntent, PendingIntent.FLAG_IMMUTABLE)

        var builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.uni_oulu_logo_black)
            .setColor(context.getColor(R.color.error))
            .setContentTitle(title)
            .setContentText(content)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(resultPendingIntent)
            .setAutoCancel(true)
            .addAction(R.drawable.ic_delete, context.getString(R.string.dismiss), dismissNotificationAction)
            .addAction(R.drawable.ic_seen, context.getString(R.string.mark_as_seen), markAsSeenAction)

        with(NotificationManagerCompat.from(context)) {
            notify(id, id.hashCode(), builder.build())
        }
    }
}