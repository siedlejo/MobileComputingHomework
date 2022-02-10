package com.siedler.jonah.mobilecomputinghomework.db.reminder

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.siedler.jonah.mobilecomputinghomework.MyApplication

@Database(entities = [Reminder::class], version = 1, exportSchema = false)
abstract class ReminderDB: RoomDatabase() {
    abstract fun reminderDao() : ReminderDao

    companion object {
        private var INSTANCE : ReminderDB = getInstance()

        fun getInstance(): ReminderDB {
            val context: Context = MyApplication.instance.applicationContext
            synchronized(ReminderDB::class) {
                INSTANCE = Room.databaseBuilder(context.applicationContext,
                    ReminderDB::class.java, "reminder.db").allowMainThreadQueries()
                    .build()
            }
            return INSTANCE
        }
    }
}