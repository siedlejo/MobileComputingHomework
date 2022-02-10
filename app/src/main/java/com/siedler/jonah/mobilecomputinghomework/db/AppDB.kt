package com.siedler.jonah.mobilecomputinghomework.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.siedler.jonah.mobilecomputinghomework.MyApplication
import com.siedler.jonah.mobilecomputinghomework.db.reminder.Reminder
import com.siedler.jonah.mobilecomputinghomework.db.reminder.ReminderDao
import com.siedler.jonah.mobilecomputinghomework.db.user.User
import com.siedler.jonah.mobilecomputinghomework.db.user.UserDao
import com.siedler.jonah.mobilecomputinghomework.helper.DateConverter

@Database(entities = [Reminder::class, User::class], version = 1, exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class AppDB: RoomDatabase() {
    abstract fun reminderDao() : ReminderDao
    abstract fun userDao() : UserDao

    companion object {
        private var INSTANCE: AppDB = getInstance()

        fun getInstance(): AppDB {
            val context: Context = MyApplication.instance.applicationContext
            synchronized(AppDB::class) {
                INSTANCE = Room.databaseBuilder(context.applicationContext,
                    AppDB::class.java, "app.db").allowMainThreadQueries()
                    .build()
            }
            return INSTANCE
        }
    }
}