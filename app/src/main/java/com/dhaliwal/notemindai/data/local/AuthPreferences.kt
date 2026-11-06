package com.dhaliwal.notemindai.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

// Extension property to initialize a single instance of Jetpack DataStore Preferences for saving auth data
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "encrypted_auth_preferences")

/**
 * Manages local storage for authentication tokens on the Android device.
 * It uses Jetpack DataStore Preferences to save data to disk, and uses CryptoManager (AES-256-GCM)
 * to encrypt tokens before saving them and decrypt tokens after loading them.
 */
class AuthPreferences(private val context: Context) {

    companion object {
        // Preference key names stored inside DataStore
        private val JWT_TOKEN_KEY = stringPreferencesKey("jwtToken")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refreshToken")
    }

    /**
     * Takes the plain access token (JWT) and refresh token, encrypts them using CryptoManager,
     * and saves the encrypted strings safely into Jetpack DataStore preferences on disk.
     */
    suspend fun saveTokens(
        accessToken: String,
        refreshToken: String
    ) {
        // Encrypt both tokens before writing to storage
        val encryptedAccess = CryptoManager.encryptString(accessToken)
        val encryptedRefresh = CryptoManager.encryptString(refreshToken)

        // Write the encrypted tokens to DataStore
        context.dataStore.edit { preferences ->
            preferences[JWT_TOKEN_KEY] = encryptedAccess
            preferences[REFRESH_TOKEN_KEY] = encryptedRefresh
        }
    }

    /**
     * Reads the encrypted access token (JWT) from DataStore, decrypts it using CryptoManager,
     * and returns the original plain text token (or null if no token exists or decryption fails).
     */
    suspend fun getAccessToken(): String? {
        // Read encrypted token string from DataStore
        val encryptedToken = context.dataStore.data.map { preferences ->
            preferences[JWT_TOKEN_KEY]
        }.first() ?: return null

        // Decrypt and return the original plain text string
        return CryptoManager.decryptToString(encryptedToken)
    }

    /**
     * Reads the encrypted refresh token from DataStore, decrypts it using CryptoManager,
     * and returns the original plain text refresh token (or null if no token exists or decryption fails).
     */
    suspend fun getRefreshToken(): String? {
        // Read encrypted refresh token string from DataStore
        val encryptedToken = context.dataStore.data.map { preferences ->
            preferences[REFRESH_TOKEN_KEY]
        }.first() ?: return null

        // Decrypt and return the original plain text string
        return CryptoManager.decryptToString(encryptedToken)
    }

    /**
     * Deletes all saved authentication tokens from Jetpack DataStore (used when logging out).
     */
    suspend fun clearTokens() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
