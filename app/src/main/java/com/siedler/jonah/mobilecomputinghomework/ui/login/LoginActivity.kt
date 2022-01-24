package com.siedler.jonah.mobilecomputinghomework.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.siedler.jonah.mobilecomputinghomework.MainActivity
import com.siedler.jonah.mobilecomputinghomework.R

class LoginActivity : AppCompatActivity() {
    private lateinit var usernameTextField: TextView
    private lateinit var passwordTextField: TextView
    private lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.login_view)

        initViews()
    }

    private fun initViews() {
        usernameTextField = findViewById(R.id.usernameField)
        passwordTextField = findViewById(R.id.passwordField)

        loginButton = findViewById(R.id.loginButton)
        loginButton.setOnClickListener { login()}

    }

    private fun login() {
        val mainActivity = Intent(this, MainActivity::class.java)
        startActivity(mainActivity)
        finish()
    }
}