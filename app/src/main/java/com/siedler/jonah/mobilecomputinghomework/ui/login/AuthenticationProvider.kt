package com.siedler.jonah.mobilecomputinghomework.ui.login

import com.siedler.jonah.mobilecomputinghomework.helper.PreferenceHelper

// A singleton to handle the authentication
object AuthenticationProvider {
    private val preferenceHelper = PreferenceHelper()
    private val encryptionHandler: EncryptionHelper = EncryptionHelper()

    fun login(username: String, password: String): Boolean {
        return if (username == "admin" && password == "admin123") {
            val key = encryptionHandler.getPasswordKey()
            preferenceHelper.storeUserName(username)
            preferenceHelper.storeUserPassword(encryptionHandler.encrypt(password, key))
            true
        } else {
            false
        }
    }
}