package com.siedler.jonah.mobilecomputinghomework.db.reminder

import androidx.room.Dao

@Dao
interface ReminderDao {
    fun insertReminder(reminder: Reminder)
}