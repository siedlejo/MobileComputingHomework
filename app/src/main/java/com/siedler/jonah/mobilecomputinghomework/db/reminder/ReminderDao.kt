package com.siedler.jonah.mobilecomputinghomework.db.reminder

import androidx.room.*
import com.siedler.jonah.mobilecomputinghomework.db.user.User

@Dao
interface ReminderDao {
    @Insert
    fun insertReminder(reminder: Reminder)

    @Update
    fun updateReminder(reminder: Reminder)

    @Delete
    fun deleteReminder(reminder: Reminder)

    @Query("Select * from reminder where reminderId=:reminderId")
    fun getReminder(reminderId: String): Reminder?

    @Query("SELECT * from reminder where creatorId=:username")
    fun getAllReminderOfUser(username: String): List<Reminder>
}