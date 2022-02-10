package com.siedler.jonah.mobilecomputinghomework.db.reminder

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.siedler.jonah.mobilecomputinghomework.db.user.User
import java.util.*

@Entity(
    tableName = "reminder",
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["userName"],
        childColumns = ["creatorId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Reminder (
    @PrimaryKey
    val reminderId: String,
    val message: String,
    val locationX: Double,
    val locationY: Double,
    val reminderTime: Date,
    val creationTime: Date,
    val creatorId: String,
    val reminderSeen: Boolean
)