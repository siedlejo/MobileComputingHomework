package com.siedler.jonah.mobilecomputinghomework.db.reminder

import android.content.Context
import androidx.room.*
import com.siedler.jonah.mobilecomputinghomework.MyApplication
import com.siedler.jonah.mobilecomputinghomework.db.user.User
import com.siedler.jonah.mobilecomputinghomework.helper.DateConverter
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

@Database(entities = [Reminder::class, User::class], version = 1, exportSchema = false)
@TypeConverters(DateConverter::class)
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