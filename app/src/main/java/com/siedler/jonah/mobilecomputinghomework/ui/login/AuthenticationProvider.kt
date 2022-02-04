package com.siedler.jonah.mobilecomputinghomework.ui.login

import com.siedler.jonah.mobilecomputinghomework.db.user.User
import com.siedler.jonah.mobilecomputinghomework.db.user.UserDB
import com.siedler.jonah.mobilecomputinghomework.helper.EncryptionHelper
import com.siedler.jonah.mobilecomputinghomework.helper.PreferenceHelper

// A singleton to handle the authentication
object
AuthenticationProvider {
    private val preferenceHelper = PreferenceHelper()
    private val encryptionHelper: EncryptionHelper = EncryptionHelper()
    private var user: User? = null

    // create a few default user, wouldn't exist in reality
    init {
        if (UserDB.getInstance().userDao().getUser("admin") == null) {
            UserDB.getInstance().userDao().insertUser(User("admin", encryptionHelper.generateHashedPassword("admin123"), "Administrator", "Admin"))
        }
    }

    /**
     * Returns the user which was authenticated during the login process
     */
    fun getAuthenticatedUser(): User {
        return user!!
    }

    fun login(username: String, password: String): Boolean {
        val user = UserDB.getInstance().userDao().getUser(username) ?: return false

        // the given password matches to the password stored in the database
        if (encryptionHelper.equalToHashedPassword(password, user.password)) {
            storeUserCredentials(username, password)
            this.user = user
            return true
        }

        return false
    }

    /**
     * This method stores the user credentials in the Shared Preferences
     * Those credentials can then be used to login via biometric/device authentication
     * The password is encrypted before it is stored
     */
    private fun storeUserCredentials(username: String, password: String) {
        val key = encryptionHelper.getPasswordKey()
        val encryptedPassword = encryptionHelper.encrypt(password, key)
        preferenceHelper.storeUserName(username)
        preferenceHelper.storeUserPassword(encryptedPassword)
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
        val key = encryptionHelper.getPasswordKey()
        return encryptionHelper.decrypt(password, key)
    }

    fun logout() {
        this.user = null
        preferenceHelper.deleteUserName()
        preferenceHelper.deleteUserPassword()
    }
}