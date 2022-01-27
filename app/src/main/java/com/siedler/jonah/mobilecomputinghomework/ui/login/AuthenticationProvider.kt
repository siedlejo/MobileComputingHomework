package com.siedler.jonah.mobilecomputinghomework.ui.login

import com.siedler.jonah.mobilecomputinghomework.helper.EncryptionHelper
import com.siedler.jonah.mobilecomputinghomework.helper.PreferenceHelper
import com.siedler.jonah.mobilecomputinghomework.ui.login.AuthenticationProvider.encryptionHandler

// A singleton to handle the authentication
object AuthenticationProvider {
    private val preferenceHelper = PreferenceHelper()
    private val encryptionHandler: EncryptionHelper = EncryptionHelper()

    fun login(username: String, password: String): Boolean {
        return if (username == "admin" && password == "admin123") {
            val key = encryptionHandler.getPasswordKey()
            val encryptedPassword = encryptionHandler.encrypt(password, key)

            preferenceHelper.storeUserName(username)
            preferenceHelper.storeUserPassword(encryptedPassword)
            true
        } else {
            false
        }
    }

    fun storedCredentialsExist(): Boolean {
        val username = getStoredUsername()
        val password = getStoredPassword()

        return username.isNotEmpty() && password.isNotEmpty()
    }

    fun getStoredUsername(): String {
        return preferenceHelper.getUserName()
    }

    fun getStoredPassword(): String {
        val password = preferenceHelper.getUserPassword()
        val key = encryptionHandler.getPasswordKey()
        return encryptionHandler.decrypt(password, key)
    }

    fun logout() {
        preferenceHelper.deleteUserName()
        preferenceHelper.deleteUserPassword()
    }
}