package com.dhaliwal.notemindai.feature.auth.data.repository

import com.dhaliwal.notemindai.feature.auth.data.remote.dto.AuthRequest
import com.dhaliwal.notemindai.core.network.NetworkModule
import com.dhaliwal.notemindai.core.security.AuthPreferences

/**
 * The data layer for login and sign-up.
 *
 * It validates the response before saving both tokens. A successful HTTP
 * response is not enough if the server did not return usable token values.
 */
class AuthRepository(
    networkModule: NetworkModule,
    private val authPreferences: AuthPreferences
) {
    private val authApi = networkModule.authApiService

    suspend fun login(
        username: String,
        password: String
    ): Result<Unit> = runCatching {
        // Retrofit performs the request away from the main thread.
        val response = authApi.login(
            AuthRequest(username, password)
        )

        check(response.success) {
            response.message.ifBlank { "Login failed" }
        }

        val data = requireNotNull(response.data) {
            "Login response contained no user data"
        }

        require(
            data.jwtToken.isNotBlank() &&
                    data.refreshToken.isNotBlank()
        ) {
            "Server returned invalid tokens"
        }

        // Save both values so protected requests and token refresh can work.
        authPreferences.jwtToken = data.jwtToken
        authPreferences.refreshToken = data.refreshToken
    }

    suspend fun signup(
        username: String,
        password: String
    ): Result<Unit> = runCatching {
        // Sign-up returns the same session data as login.
        val response = authApi.signup(
            AuthRequest(username, password)
        )

        check(response.success) {
            response.message.ifBlank { "Signup failed" }
        }

        val data = requireNotNull(response.data) {
            "Signup response contained no user data"
        }

        require(
            data.jwtToken.isNotBlank() &&
                    data.refreshToken.isNotBlank()
        ) {
            "Server returned invalid tokens"
        }

        authPreferences.jwtToken = data.jwtToken
        authPreferences.refreshToken = data.refreshToken
    }
}
