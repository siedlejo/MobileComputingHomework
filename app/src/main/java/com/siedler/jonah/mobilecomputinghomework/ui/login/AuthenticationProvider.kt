package com.siedler.jonah.mobilecomputinghomework.ui.login

import com.siedler.jonah.mobilecomputinghomework.db.AppDB
import com.siedler.jonah.mobilecomputinghomework.db.user.User
import com.siedler.jonah.mobilecomputinghomework.helper.EncryptionHelper
import com.siedler.jonah.mobilecomputinghomework.helper.PreferenceHelper
import com.siedler.jonah.mobilecomputinghomework.helper.notifications.NotificationHelper

// A singleton to handle the authentication
object
AuthenticationProvider {
    private val preferenceHelper = PreferenceHelper()
    private val encryptionHelper: EncryptionHelper = EncryptionHelper()
    private var user: User? = null

    // create a few default user, wouldn't exist in reality
    init {
        if (!userExists("admin")) {
            addUser("admin", "admin123", "Administrator", "Admin")
        }
    }

    fun addUser(username: String, password: String, firstName: String, lastName: String) {
        val encryptedPassword = encryptionHelper.generateHashedPassword(password)
        val newUser = User(username, encryptedPassword, firstName, lastName)
        AppDB.getInstance().userDao().insertUser(newUser)
    }

    fun userExists(username: String): Boolean {
        return AppDB.getInstance().userDao().getUser(username) != null
    }

    fun isLoggedIn(): Boolean {
        return getAuthenticatedUser() != null
    }

    /**
     * Returns the user which was authenticated during the login process
     */
    fun getAuthenticatedUser(): User? {
        return user
    }

    fun login(username: String, password: String): Boolean {
        val user = AppDB.getInstance().userDao().getUser(username) ?: return false

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
        NotificationHelper.cancelAllScheduledNotifications()
        NotificationHelper.removeAllNotifications()
    }
}