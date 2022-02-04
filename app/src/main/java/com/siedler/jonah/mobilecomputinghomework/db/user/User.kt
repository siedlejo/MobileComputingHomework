package com.siedler.jonah.mobilecomputinghomework.db.user

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class User(
    @PrimaryKey
    val userName: String,
    val password: String,
    val firstName: String,
    val lastName: String
)