package com.siedler.jonah.mobilecomputinghomework.ui.reminder

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.siedler.jonah.mobilecomputinghomework.R

const val SELECTED_LOCATION_X = "SELECTED_LOCATION_X"
const val SELECTED_LOCATION_Y = "SELECTED_LOCATION_Y"

class SelectLocationActivity: AppCompatActivity() {
    private lateinit var selectLocationButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.select_location_activity)

        selectLocationButton = findViewById(R.id.selectLocationButton)
        selectLocationButton.setOnClickListener {
            val resultIntent = Intent()
            resultIntent.putExtra(SELECTED_LOCATION_X, 9.173581666666667F)
            resultIntent.putExtra(SELECTED_LOCATION_Y, 48.781513333333336F)
            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }
}