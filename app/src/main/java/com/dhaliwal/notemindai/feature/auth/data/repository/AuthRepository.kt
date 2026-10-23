package com.dhaliwal.notemindai.feature.auth.data.repository

import com.dhaliwal.notemindai.feature.auth.data.remote.dto.AuthRequest
import com.dhaliwal.notemindai.core.network.NetworkModule
import com.dhaliwal.notemindai.core.security.AuthPreferences

class AuthRepository(
    networkModule: NetworkModule,
    private val authPreferences: AuthPreferences
) {
    private val authApi = networkModule.authApiService

    suspend fun login(
        username: String,
        password: String
    ): Result<Unit> = runCatching {
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

        authPreferences.jwtToken = data.jwtToken
        authPreferences.refreshToken = data.refreshToken
    }

    suspend fun signup(
        username: String,
        password: String
    ): Result<Unit> = runCatching {
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
