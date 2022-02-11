package com.siedler.jonah.mobilecomputinghomework.ui.reminder

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.siedler.jonah.mobilecomputinghomework.R
import com.siedler.jonah.mobilecomputinghomework.db.AppDB
import com.siedler.jonah.mobilecomputinghomework.db.reminder.Reminder
import com.siedler.jonah.mobilecomputinghomework.ui.login.AuthenticationProvider
import java.util.*

const val REMINDER_INTENT_EXTRA_KEY: String = "REMINDER_INTENT_EXTRA"

class AddReminderActivity: AppCompatActivity() {
    private lateinit var timePicker: TimePicker
    private lateinit var datePicker: DatePicker
    private lateinit var messageEditText: EditText
    private lateinit var locationXEditText: EditText
    private lateinit var locationYEditText: EditText
    private lateinit var cancelButton: Button
    private lateinit var saveButton: Button
    private lateinit var textToSpeechButton: ImageButton

    private var reminder: Reminder? = null

    private var textToSpeechResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val resultText = result?.get(0)
            messageEditText.setText(resultText)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.add_reminder)

        initViews()
        initPassedReminder()
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
            saveReminder()
        }

        textToSpeechButton = findViewById(R.id.textToSpeechButton)
        textToSpeechButton.setOnClickListener {
            getSpeechInput()
        }
    }

    private fun getSpeechInput()
    {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())

        textToSpeechResultLauncher.launch(intent)
    }

    /**
     * Is called in case a reminder is passed to the activity
     * This means that we edit a reminder instead of creating a new one
     */
    private fun initPassedReminder() {
        val reminderId = intent.getLongExtra(REMINDER_INTENT_EXTRA_KEY, -1)
        reminder = AppDB.getInstance().reminderDao().getReminder(reminderId)

        if (reminder == null) {
            return
        }

        title = getString(R.string.edit_reminder_view_title)
        val calendar = Calendar.getInstance()
        calendar.time = (reminder!!.reminderTime)
        timePicker.hour = calendar.get(Calendar.HOUR_OF_DAY)
        timePicker.minute = calendar.get(Calendar.MINUTE)
        datePicker.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        messageEditText.setText(reminder!!.message)
        locationXEditText.setText(reminder!!.locationX?.toString() ?: "")
        locationYEditText.setText(reminder!!.locationY?.toString() ?: "")
    }

    private fun saveReminder() {
        if (reminder != null) {
            reminder!!.message = messageEditText.text.toString()
            reminder!!.locationX = locationXEditText.text.toString().toDoubleOrNull()
            reminder!!.locationY = locationYEditText.text.toString().toDoubleOrNull()
            reminder!!.creationTime = Date()
            reminder!!.creatorId = AuthenticationProvider.getAuthenticatedUser().userName
            reminder!!.reminderSeen = false
            reminder!!.reminderTime = getReminderTime()
            AppDB.getInstance().reminderDao().updateReminder(reminder!!)
        } else {
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
        }

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