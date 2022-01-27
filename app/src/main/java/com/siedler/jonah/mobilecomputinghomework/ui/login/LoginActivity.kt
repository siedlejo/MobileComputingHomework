package com.siedler.jonah.mobilecomputinghomework.ui.login

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.biometrics.BiometricManager
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && checkBiometricSupport() && AuthenticationProvider.storedCredentialsExist()) {
            showBiometricLogin()
        }
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


    @RequiresApi(Build.VERSION_CODES.P)
    fun showBiometricLogin() {
        val biometricPromptBuilder = BiometricPrompt.Builder(this)
            .setTitle(getString(R.string.alternative_authentication_title))
            .setSubtitle(getString(R.string.alternative_authentication_subtitle))
            .setNegativeButton("Cancel", this.mainExecutor, { _, _ ->
            })

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            biometricPromptBuilder.setConfirmationRequired(false)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            biometricPromptBuilder.setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
        }

        val biometricPrompt : BiometricPrompt = biometricPromptBuilder.build()
        biometricPrompt.authenticate(CancellationSignal(), mainExecutor, authenticationCallback)
    }

    private val authenticationCallback: BiometricPrompt.AuthenticationCallback
        get() =
            @RequiresApi(Build.VERSION_CODES.P)
            object: BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
                    super.onAuthenticationError(errorCode, errString)
                }
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
                    super.onAuthenticationSucceeded(result)
                    usernameTextField.text = AuthenticationProvider.getStoredUsername()
                    passwordTextField.text = AuthenticationProvider.getStoredPassword()
                    login()
                }
            }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun checkBiometricSupport(): Boolean {
        val keyguardManager : KeyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

        val deviceIsSecured = keyguardManager.isDeviceSecure
        val appHasBiometricPermission = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.USE_BIOMETRIC) == PackageManager.PERMISSION_GRANTED
        val hasFingerprintFeature = packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)
        val hasFaceRecognitionFeature = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            packageManager.hasSystemFeature(PackageManager.FEATURE_FACE)
        } else {
            false
        }

        return deviceIsSecured && appHasBiometricPermission && (hasFingerprintFeature || hasFaceRecognitionFeature)
    }
}