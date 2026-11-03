package com.dhaliwal.notemindai.data.local

import android.content.Context
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

/**
 * Manages secure local storage for authentication tokens on the Android device.
 * Uses EncryptedSharedPreferences to encrypt keys and token values.
 */
class AuthPreferences(context: Context) {

    // Encrypted SharedPreferences instance for secure key-value storage
    private val sharedPreferences =
        EncryptedSharedPreferences.create(
            "secure_auth_prefs",
            MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

    /**
     * Saves the access token (JWT) and refresh token securely.
     */
    fun saveTokens(
        accessToken: String,
        refreshToken: String
    ) {
        sharedPreferences.edit {
            putString("jwtToken", accessToken)
                .putString("refreshToken", refreshToken)
        }
    }

    /**
     * Retrieves the saved JWT access token, or null if not set.
     */
    fun getAccessToken(): String? {
        return sharedPreferences.getString("jwtToken", null)
    }

    /**
     * Retrieves the saved refresh token, or null if not set.
     */
    fun getRefreshToken(): String? {
        return sharedPreferences.getString("refreshToken", null)
    }

    /**
     * Clears all saved authentication tokens (used during logout).
     */
    fun clearTokens() {
        sharedPreferences.edit().clear().apply()
    }
}
