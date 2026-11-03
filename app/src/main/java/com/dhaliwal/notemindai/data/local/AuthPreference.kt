package com.dhaliwal.notemindai.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

// Extension property for DataStore singleton instance per Context
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_preferences")

/**
 * Manages local storage for authentication tokens on the Android device.
 * Uses Jetpack DataStore Preferences to store keys and token values asynchronously and safely.
 */
class AuthPreferences(private val context: Context) {

    companion object {
        private val JWT_TOKEN_KEY = stringPreferencesKey("jwtToken")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refreshToken")
    }

    /**
     * Saves the access token (JWT) and refresh token into DataStore.
     */
    suspend fun saveTokens(
        accessToken: String,
        refreshToken: String
    ) {
        context.dataStore.edit { preferences ->
            preferences[JWT_TOKEN_KEY] = accessToken
            preferences[REFRESH_TOKEN_KEY] = refreshToken
        }
    }

    /**
     * Retrieves the saved JWT access token, or null if not set.
     */
    fun getAccessToken(): String? = runBlocking {
        context.dataStore.data.map { preferences ->
            preferences[JWT_TOKEN_KEY]
        }.first()
    }

    /**
     * Retrieves the saved refresh token, or null if not set.
     */
    fun getRefreshToken(): String? = runBlocking {
        context.dataStore.data.map { preferences ->
            preferences[REFRESH_TOKEN_KEY]
        }.first()
    }

    /**
     * Clears all saved authentication tokens from DataStore (used during logout).
     */
    suspend fun clearTokens() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
