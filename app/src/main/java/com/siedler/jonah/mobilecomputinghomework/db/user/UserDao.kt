package com.siedler.jonah.mobilecomputinghomework.db.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {
    @Insert
    fun insertUser(users: User)

    @Query("Select * from user where userName=:username")
    fun getUser(username: String): User?
}