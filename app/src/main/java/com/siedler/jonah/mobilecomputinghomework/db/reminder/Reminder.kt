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
data class Reminder(
    @PrimaryKey
    val reminderId: String = UUID.randomUUID().toString(),
    var message: String,
    var locationX: Double?,
    var locationY: Double?,
    var reminderTime: Date?,
    var creationTime: Date,
    var creatorId: String,
    var reminderSeen: Boolean
)