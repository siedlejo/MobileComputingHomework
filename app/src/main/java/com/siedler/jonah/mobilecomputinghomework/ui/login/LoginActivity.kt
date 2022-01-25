package com.siedler.jonah.mobilecomputinghomework.ui.login

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.siedler.jonah.mobilecomputinghomework.MainActivity
import com.siedler.jonah.mobilecomputinghomework.R

class LoginActivity : AppCompatActivity() {
    private lateinit var usernameTextField: TextView
    private lateinit var passwordTextField: TextView
    private lateinit var loginButton: Button
    private lateinit var loginErrorText: TextView

    private var username: String = ""
    private var password: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.login_view)

        initViews()
    }

    private fun initViews() {
        usernameTextField = findViewById(R.id.usernameField)
        usernameTextField.addTextChangedListener { text ->
            username = text.toString()
            loginErrorText.visibility = View.INVISIBLE
            setLoginButtonAvailability()
        }

        passwordTextField = findViewById(R.id.passwordField)
        passwordTextField.addTextChangedListener { text ->
            password = text.toString()
            loginErrorText.visibility = View.INVISIBLE
            setLoginButtonAvailability()
        }

        loginButton = findViewById(R.id.loginButton)
        loginButton.setOnClickListener { login()}

        loginErrorText = findViewById(R.id.loginErrorText)
    }

    private fun login() {
        if (AuthenticationProvider.login(username, password)) {
            loginSuccessful()
        } else {
            loginFailed()
        }
    }

    private fun setLoginButtonAvailability() {
        loginButton.isEnabled = username.isNotEmpty() && password.isNotEmpty()
    }

    private fun loginSuccessful() {
        val mainActivity = Intent(this, MainActivity::class.java)
        startActivity(mainActivity)
        finish()
    }

    private fun loginFailed() {
        passwordTextField.text = ""
        passwordTextField.clearFocus()
        loginErrorText.visibility = View.VISIBLE
    }
}