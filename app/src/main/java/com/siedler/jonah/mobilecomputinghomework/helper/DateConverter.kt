package com.siedler.jonah.mobilecomputinghomework.helper

import androidx.room.TypeConverter
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object DateConverter {
    var df: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss'Z'", Locale.getDefault())

    @TypeConverter
    fun timeToDate(value: String): Date? {
        return try {
            df.parse(value)
        } catch (e: ParseException) {
            null
        }
    }

    @TypeConverter
    fun dateToTime(value: Date): String? {
        return df.format(value)
    }
}