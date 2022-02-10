package com.siedler.jonah.mobilecomputinghomework.ui.reminder

import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import com.siedler.jonah.mobilecomputinghomework.R
import com.siedler.jonah.mobilecomputinghomework.db.AppDB
import com.siedler.jonah.mobilecomputinghomework.db.reminder.Reminder
import com.siedler.jonah.mobilecomputinghomework.ui.login.AuthenticationProvider
import java.util.*

class AddReminderActivity: AppCompatActivity() {
    private lateinit var timePicker: TimePicker
    private lateinit var datePicker: DatePicker
    private lateinit var messageEditText: EditText
    private lateinit var locationXEditText: EditText
    private lateinit var locationYEditText: EditText

    private lateinit var cancelButton: Button
    private lateinit var saveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.add_reminder)

        initViews()
    }

    private fun initViews() {
        timePicker = findViewById(R.id.reminderTimePicker)
        datePicker = findViewById(R.id.reminderDatePicker)
        messageEditText = findViewById(R.id.messageEditText)
        locationXEditText = findViewById(R.id.locationXEditText)
        locationYEditText = findViewById(R.id.locationYEditText)

        cancelButton = findViewById(R.id.cancelReminderButton)
        cancelButton.setOnClickListener {
            finish()
        }

        saveButton = findViewById(R.id.saveReminderButton)
        saveButton.setOnClickListener {
            createReminder()
        }
    }

    private fun createReminder() {
        val newReminder = Reminder(
            message = messageEditText.text.toString(),
            locationX = locationXEditText.text.toString().toDoubleOrNull(),
            locationY = locationYEditText.text.toString().toDoubleOrNull(),
            creationTime = Date(),
            creatorId = AuthenticationProvider.getAuthenticatedUser().userName,
            reminderSeen = false,
            reminderTime = getReminderTime()
        )

        AppDB.getInstance().reminderDao().insertReminder(newReminder)
        finish()
    }

    private fun getReminderTime(): Date {
        val calendar: Calendar = GregorianCalendar(
            datePicker.year,
            datePicker.month,
            datePicker.dayOfMonth,
            timePicker.hour,
            timePicker.minute
        )
        return calendar.time
    }
}