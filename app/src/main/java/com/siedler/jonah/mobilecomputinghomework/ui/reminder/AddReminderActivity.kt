package com.siedler.jonah.mobilecomputinghomework.ui.reminder

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.siedler.jonah.mobilecomputinghomework.R

class AddReminderActivity: AppCompatActivity() {
    private lateinit var cancelButton: Button
    private lateinit var saveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.add_reminder)

        initViews()
    }

    private fun initViews() {
        cancelButton = findViewById(R.id.cancelReminderButton)
        cancelButton.setOnClickListener {
            finish()
        }

        saveButton = findViewById(R.id.saveReminderButton)
        saveButton.setOnClickListener {
        }
    }
}