package com.siedler.jonah.mobilecomputinghomework.helper

import android.content.Context
import android.content.SharedPreferences
import com.siedler.jonah.mobilecomputinghomework.MyApplication

const val SHARED_PREFERENCES_KEY = "com.siedler.jonah.mobilecomputinghomework.MySharedPreferences"
const val USER_NAME = "UserName"
const val USER_PASSWORD_NAME = "UserPassword"

class PreferenceHelper {
    private val context: Context = MyApplication.instance.applicationContext
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)

    fun storeUserName(value: String) {
        print(sharedPreferences.edit().putString(USER_NAME, value).commit())
    }

    fun getUserName(): String {
        return sharedPreferences.getString(USER_NAME, "")!!
    }

    fun storeUserPassword(value: String) {
        print(sharedPreferences.edit().putString(USER_PASSWORD_NAME, value).commit())
    }

    fun getUserPassword(): String {
        return sharedPreferences.getString(USER_PASSWORD_NAME, "")!!
    }
}