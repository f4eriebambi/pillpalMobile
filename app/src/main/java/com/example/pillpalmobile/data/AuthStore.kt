package com.example.pillpalmobile.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore("auth")

object AuthStore {
    private val TOKEN_KEY = stringPreferencesKey("auth_token")


    @Volatile
    private var cachedToken: String? = null


    suspend fun loadToken(context: Context) {
        cachedToken = context.dataStore.data
            .map { prefs -> prefs[TOKEN_KEY] }
            .first()
    }

    fun getCachedToken(): String? = cachedToken


    suspend fun getToken(context: Context): String? {
        val token = context.dataStore.data
            .map { prefs -> prefs[TOKEN_KEY] }
            .first()

        cachedToken = token
        return token
    }

    suspend fun saveToken(context: Context, token: String) {
        context.dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
        }
        cachedToken = token
    }

    suspend fun clearToken(context: Context) {
        context.dataStore.edit { prefs ->
            prefs.remove(TOKEN_KEY)
        }
        cachedToken = null
    }
}
