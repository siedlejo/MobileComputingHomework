package com.siedler.jonah.mobilecomputinghomework.ui.usersignup

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.siedler.jonah.mobilecomputinghomework.R
import com.siedler.jonah.mobilecomputinghomework.ui.login.AuthenticationProvider
import java.util.regex.Pattern

class UserSignupActivity: AppCompatActivity() {
    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var repeatPasswordEditText: EditText
    private lateinit var signupButton: Button

    private lateinit var firstNameErrorView: TextView
    private lateinit var lastNameErrorView: TextView
    private lateinit var usernameErrorView: TextView
    private lateinit var passwordErrorView: TextView
    private lateinit var repeatPasswordErrorView: TextView

    private var firstName: String = ""
    private var lastName: String = ""
    private var username: String = ""
    private var password: String = ""
    private var repeatPassword: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.user_signup)

        initViews()
    }

    private fun initViews() {
        firstNameEditText = findViewById(R.id.firstNameEditText)
        firstNameEditText.addTextChangedListener { text ->
            firstName = text.toString()
            setSignupButtonAvailability()
        }
        lastNameEditText = findViewById(R.id.lastNameEditText)
        lastNameEditText.addTextChangedListener { text ->
            lastName = text.toString()
            setSignupButtonAvailability()
        }
        usernameEditText = findViewById(R.id.usernameEditText)
        usernameEditText.addTextChangedListener { text ->
            username = text.toString()
            setSignupButtonAvailability()
        }
        passwordEditText = findViewById(R.id.passwordEditText)
        passwordEditText.addTextChangedListener { text ->
            password = text.toString()
            setSignupButtonAvailability()
        }
        repeatPasswordEditText = findViewById(R.id.repeatPasswordEditText)
        repeatPasswordEditText.addTextChangedListener { text ->
            repeatPassword = text.toString()
            setSignupButtonAvailability()
        }

        signupButton = findViewById(R.id.signupButton)
        signupButton.isEnabled = false
        signupButton.setOnClickListener { signup() }

        firstNameErrorView = findViewById(R.id.invalidFirstNameText)
        lastNameErrorView = findViewById(R.id.invalidLastNameText)
        usernameErrorView = findViewById(R.id.invalidUsernameText)
        passwordErrorView = findViewById(R.id.invalidPasswordText)
        repeatPasswordErrorView = findViewById(R.id.invalidRepeatPasswordText)
    }

    private fun signup() {
        AuthenticationProvider.addUser(username, password, firstName, lastName)
        finish()
    }

    private fun setSignupButtonAvailability() {
        val displayButton = (firstName.isNotEmpty() and firstNameValid()
                and lastName.isNotEmpty() and lastNameValid()
                and username.isNotEmpty() and usernameValid()
                and password.isNotEmpty() and passwordValid()
                and repeatPassword.isNotEmpty() and repeatPasswordValid())
        signupButton.isEnabled = displayButton
    }

    private fun firstNameValid(): Boolean {
        if (containsWhitespace(firstName)) {
            firstNameErrorView.visibility = View.VISIBLE
        } else {
            firstNameErrorView.visibility = View.INVISIBLE
        }
        return firstNameErrorView.visibility == View.INVISIBLE
    }

    private fun lastNameValid(): Boolean {
        if (containsWhitespace(lastName)) {
            lastNameErrorView.visibility = View.VISIBLE
        } else {
            lastNameErrorView.visibility = View.INVISIBLE
        }
        return lastNameErrorView.visibility == View.INVISIBLE
    }

    private fun usernameValid(): Boolean {
        when {
            containsWhitespace(username) -> {
                usernameErrorView.text = getString(R.string.invalid_username_spaces)
                usernameErrorView.visibility = View.VISIBLE
            }
            AuthenticationProvider.userExists(username) -> {
                usernameErrorView.text = getString(R.string.invalid_username_already_exists)
                usernameErrorView.visibility = View.VISIBLE
            }
            else -> {
                usernameErrorView.visibility = View.INVISIBLE
            }
        }
        return usernameErrorView.visibility == View.INVISIBLE
    }

    private fun passwordValid(): Boolean {
        when {
            containsWhitespace(password) -> {
                passwordErrorView.text = getString(R.string.invalid_password_error_spaces)
                passwordErrorView.visibility = View.VISIBLE
            }
            password.length <= 5 -> {
                passwordErrorView.text = getString(R.string.invalid_password_length)
                passwordErrorView.visibility = View.VISIBLE
            }
            else -> {

                passwordErrorView.visibility = View.INVISIBLE
            }
        }
        return passwordErrorView.visibility == View.INVISIBLE
    }

    private fun repeatPasswordValid(): Boolean {
        if (password != repeatPassword) {
            repeatPasswordErrorView.visibility = View.VISIBLE
        } else {
            repeatPasswordErrorView.visibility = View.INVISIBLE
        }
        return repeatPasswordErrorView.visibility == View.INVISIBLE
    }

    private fun containsWhitespace(word: String): Boolean {
        val wordPattern: Pattern = Pattern.compile("\\s+")
        return wordPattern.matcher(word).find()
    }
}