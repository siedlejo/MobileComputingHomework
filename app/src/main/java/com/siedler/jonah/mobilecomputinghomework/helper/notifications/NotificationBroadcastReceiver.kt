package com.siedler.jonah.mobilecomputinghomework.helper.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.siedler.jonah.mobilecomputinghomework.db.AppDB
import com.siedler.jonah.mobilecomputinghomework.ui.home.HomeFragment

const val MARK_AS_SEEN_ACTION = "MARK_AS_SEEN_ACTION"
const val DISMISS_ACTION = "DISMISS_ACTION"
const val NOTIFICATION_ID_MARK_AS_SEEN_ACTION = "NOTIFICATION_ID_MARK_AS_SEEN_ACTION"
const val NOTIFICATION_ID_DISMISS_ACTION = "NOTIFICATION_ID_DISMISS_ACTION"

class NotificationBroadcastReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent!!.action

        if (MARK_AS_SEEN_ACTION == action) {
            val reminderId = intent.getStringExtra(NOTIFICATION_ID_MARK_AS_SEEN_ACTION) ?: ""
            markAsSeen(reminderId)
        } else if (DISMISS_ACTION == action) {
            val reminderId = intent.getStringExtra(NOTIFICATION_ID_DISMISS_ACTION) ?: ""
            dismissNotification(reminderId)
        }
    }

    private fun markAsSeen(reminderId: String) {
        val reminder = AppDB.getInstance().reminderDao().getReminder(reminderId)

        reminder?.let {
            it.reminderSeen = true
            AppDB.getInstance().reminderDao().updateReminder(reminder)
            dismissNotification(reminderId)
            HomeFragment.Instance?.getReminderListFromDB()
        }
    }

    private fun dismissNotification(reminderId: String) {
        NotificationHelper.removeNotification(reminderId)
    }
}