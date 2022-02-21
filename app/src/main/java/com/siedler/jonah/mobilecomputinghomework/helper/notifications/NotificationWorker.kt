package com.siedler.jonah.mobilecomputinghomework.helper.notifications

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

const val NOTIFICATION_TITLE_KEY = "NOTIFICATION_TITLE_KEY"
const val NOTIFICATION_CONTENT_KEY = "NOTIFICATION_CONTENT_KEY"

class NotificationWorker(context: Context, workerParams: WorkerParameters) : Worker(context,
    workerParams
) {

    override fun doWork(): Result {
        val title = inputData.getString(NOTIFICATION_TITLE_KEY) ?: ""
        val content = inputData.getString(NOTIFICATION_CONTENT_KEY) ?: ""

        NotificationHelper.sendNotification(title, content)
        return Result.success()
    }
}