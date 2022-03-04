package com.siedler.jonah.mobilecomputinghomework.ui.reminder

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.siedler.jonah.mobilecomputinghomework.R
import com.siedler.jonah.mobilecomputinghomework.db.AppDB
import com.siedler.jonah.mobilecomputinghomework.db.reminder.Reminder
import com.siedler.jonah.mobilecomputinghomework.helper.notifications.NotificationHelper
import com.siedler.jonah.mobilecomputinghomework.ui.login.AuthenticationProvider
import java.util.*
import java.util.concurrent.TimeUnit

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

    private lateinit var timeArea: ConstraintLayout
    private lateinit var addTimeButton: ImageButton
    private lateinit var removeTimeButton: ImageButton
    private lateinit var locationArea: LinearLayout
    private lateinit var addLocationButton: ImageButton
    private lateinit var removeLocationButton: ImageButton

    private var reminder: Reminder? = null

    private var textToSpeechResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val resultText = result?.get(0)
            messageEditText.setText(resultText)
        }
    }

    private var selectLocationResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent = result.data!!
            val locationX = data.getFloatExtra(SELECTED_LOCATION_X, 0F)
            val locationY = data.getFloatExtra(SELECTED_LOCATION_Y, 0F)
            addLocation(locationX, locationY)
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

        timeArea = findViewById(R.id.timeArea)
        addTimeButton = findViewById(R.id.addTimeButton)
        addTimeButton.setOnClickListener { addTime(Date()) }
        removeTimeButton = findViewById(R.id.removeTimeButton)
        removeTimeButton.setOnClickListener { removeTime() }

        locationArea = findViewById(R.id.locationArea)
        addLocationButton = findViewById(R.id.addLocationButton)
        addLocationButton.setOnClickListener { selectLocationResultLauncher.launch(Intent(applicationContext, SelectLocationActivity::class.java)) }
        removeLocationButton = findViewById(R.id.removeLocationButton)
        removeLocationButton.setOnClickListener { removeLocation() }
    }

    private fun addTime(time: Date) {
        val calendar = Calendar.getInstance()
        calendar.time = (time)
        timePicker.hour = calendar.get(Calendar.HOUR_OF_DAY)
        timePicker.minute = calendar.get(Calendar.MINUTE)
        datePicker.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

        addTimeButton.visibility = View.GONE
        timeArea.visibility = View.VISIBLE
    }

    private fun removeTime() {
        addTimeButton.visibility = View.VISIBLE
        timeArea.visibility = View.GONE
    }

    private fun addLocation(locationX: Float, locationY: Float) {
        locationXEditText.setText(locationX.toString())
        locationYEditText.setText(locationY.toString())

        addLocationButton.visibility = View.GONE
        locationArea.visibility = View.VISIBLE
    }

    private fun removeLocation() {
        locationXEditText.setText("")
        locationYEditText.setText("")

        addLocationButton.visibility = View.VISIBLE
        locationArea.visibility = View.GONE
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
        val reminderId = intent.getStringExtra(REMINDER_INTENT_EXTRA_KEY) ?: ""
        reminder = AppDB.getInstance().reminderDao().getReminder(reminderId)

        if (reminder == null) {
            return
        }

        title = getString(R.string.edit_reminder_view_title)

        reminder!!.reminderTime?.let {
            addTime(it)
        }
        messageEditText.setText(reminder!!.message)
        if (reminder!!.locationX != null && reminder!!.locationY != null) {
            addLocation(reminder!!.locationX!!.toFloat(), reminder!!.locationY!!.toFloat())
        }
    }

    private fun saveReminder() {
        if (reminder != null) {
            reminder!!.message = messageEditText.text.toString()
            reminder!!.locationX = locationXEditText.text.toString().toDoubleOrNull()
            reminder!!.locationY = locationYEditText.text.toString().toDoubleOrNull()
            reminder!!.creationTime = Date()
            reminder!!.creatorId = AuthenticationProvider.getAuthenticatedUser()!!.userName
            reminder!!.reminderSeen = false
            reminder!!.reminderTime = getReminderTime()
            AppDB.getInstance().reminderDao().updateReminder(reminder!!)
        } else {
            reminder = Reminder(
                message = messageEditText.text.toString(),
                locationX = locationXEditText.text.toString().toDoubleOrNull(),
                locationY = locationYEditText.text.toString().toDoubleOrNull(),
                creationTime = Date(),
                creatorId = AuthenticationProvider.getAuthenticatedUser()!!.userName,
                reminderSeen = false,
                reminderTime = getReminderTime()
            )
            AppDB.getInstance().reminderDao().insertReminder(reminder!!)
        }

        scheduleReminderNotification(reminder!!)
        finish()
    }

    private fun scheduleReminderNotification(reminder: Reminder) {
        reminder.reminderTime?.let {
            val time = it.time - Date().time
            val timeInSeconds = TimeUnit.MILLISECONDS.toSeconds(time)

            NotificationHelper.scheduleNotification(timeInSeconds, reminder.reminderId, reminder.message, getString(R.string.tap_to_open_in_app))
        }
    }

    private fun getReminderTime(): Date? {
        return if (timeArea.visibility == View.VISIBLE) {
            val calendar: Calendar = GregorianCalendar(
                datePicker.year,
                datePicker.month,
                datePicker.dayOfMonth,
                timePicker.hour,
                timePicker.minute
            )
            calendar.time
        } else {
            null
        }
    }
}