package com.siedler.jonah.mobilecomputinghomework.db.user

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.siedler.jonah.mobilecomputinghomework.MyApplication

@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class UserDB : RoomDatabase() {
    abstract fun userDao() : UserDao

    companion object {
        private var INSTANCE: UserDB = getInstance()

        fun getInstance(): UserDB {
            val context: Context = MyApplication.instance.applicationContext
            synchronized(UserDB::class) {
                INSTANCE = Room.databaseBuilder(context.applicationContext,
                    UserDB::class.java, "user.db").allowMainThreadQueries()
                    .build()
            }
            return INSTANCE
        }
    }
}