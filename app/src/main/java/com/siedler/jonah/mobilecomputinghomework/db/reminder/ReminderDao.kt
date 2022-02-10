package com.siedler.jonah.mobilecomputinghomework.db.reminder

import androidx.room.*

@Dao
interface ReminderDao {
    @Insert
    fun insertReminder(reminder: Reminder)

    @Update
    fun updateReminder(reminder: Reminder)

    @Delete
    fun deleteReminder(reminder: Reminder)

    @Query("SELECT * from reminder")
    fun getAllReminder(): List<Reminder>
}