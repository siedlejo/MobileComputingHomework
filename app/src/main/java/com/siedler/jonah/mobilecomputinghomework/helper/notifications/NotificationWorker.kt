package com.siedler.jonah.mobilecomputinghomework.helper.notifications

import android.content.Context
import android.os.Handler
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.siedler.jonah.mobilecomputinghomework.ui.home.HomeFragment
import kotlinx.coroutines.NonCancellable
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

const val NOTIFICATION_ID_KEY = "NOTIFICATION_ID_KEY"
const val NOTIFICATION_TITLE_KEY = "NOTIFICATION_TITLE_KEY"
const val NOTIFICATION_CONTENT_KEY = "NOTIFICATION_CONTENT_KEY"

class NotificationWorker(context: Context, workerParams: WorkerParameters) : Worker(context,
    workerParams
) {

    override fun doWork(): Result {
        val id = inputData.getString( NOTIFICATION_ID_KEY) ?: ""
        val title = inputData.getString(NOTIFICATION_TITLE_KEY) ?: ""
        val content = inputData.getString(NOTIFICATION_CONTENT_KEY) ?: ""

        NotificationHelper.sendNotification(id, title, content)

        // trigger an update to the home fragment in case it is running to automatically update the list
        Executors.newSingleThreadScheduledExecutor().schedule({
            HomeFragment.Instance?.getReminderListFromDB()
        }, 1, TimeUnit.SECONDS)
        return Result.success()
    }
}