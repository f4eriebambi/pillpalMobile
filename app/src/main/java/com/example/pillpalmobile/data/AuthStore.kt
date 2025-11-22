package com.example.pillpalmobile.data

import android.content.Context
import com.example.pillpalmobile.model.LoginResponse

object AuthStore {

    private const val PREFS_NAME = "pillpal_auth"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_FULL_NAME = "full_name"
    private const val KEY_USERNAME = "username"
    private const val KEY_EMAIL = "email"
    private const val KEY_TIMEZONE = "timezone"

    // user actual en memoria
    var currentUser: LoginResponse? = null
        private set

    fun setCurrentUser(context: Context, user: LoginResponse) {
        currentUser = user
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putInt(KEY_USER_ID, user.user_id ?: -1)
            .putString(KEY_FULL_NAME, user.full_name)
            .putString(KEY_USERNAME, user.username)
            .putString(KEY_EMAIL, user.email)
            .putString(KEY_TIMEZONE, user.timezone)
            .apply()
    }

    fun loadUser(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val id = prefs.getInt(KEY_USER_ID, -1)
        if (id == -1) {
            currentUser = null
            return
        }
        currentUser = LoginResponse(
            user_id = id,
            full_name = prefs.getString(KEY_FULL_NAME, null),
            username = prefs.getString(KEY_USERNAME, null),
            email = prefs.getString(KEY_EMAIL, null),
            timezone = prefs.getString(KEY_TIMEZONE, null),
            error = null
        )
    }

    fun clear(context: Context) {
        currentUser = null
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }
}
