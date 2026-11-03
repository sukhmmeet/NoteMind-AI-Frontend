package com.dhaliwal.notemindai.data.repository

import com.dhaliwal.notemindai.data.local.AuthPreferences
import com.dhaliwal.notemindai.data.model.AuthRequest
import com.dhaliwal.notemindai.data.model.AuthResponse
import com.dhaliwal.notemindai.data.model.LogoutResponse
import com.dhaliwal.notemindai.data.model.RefreshTokenRequest
import com.dhaliwal.notemindai.data.model.RefreshTokenResponse
import com.dhaliwal.notemindai.network.api.AuthApiService
import retrofit2.HttpException
import java.io.IOException

/**
 * Repository class that handles authentication operations.
 * Communicates with the remote API via AuthApiService and saves/clears tokens locally using AuthPreferences.
 */
class AuthRepository(
    private val authApi: AuthApiService,
    private val authPreferences: AuthPreferences
) {

    /**
     * Signs up a new user with username and password.
     * On success, saves the returned access and refresh tokens locally.
     */
    suspend fun signup(request: AuthRequest): Result<AuthResponse> {
        return try {
            val apiResponse = authApi.register(request)
            val authData = apiResponse.data
            if (apiResponse.success && authData != null) {
                authPreferences.saveTokens(
                    accessToken = authData.jwtToken ?: "",
                    refreshToken = authData.refreshToken ?: ""
                )
                Result.success(authData)
            } else {
                Result.failure(Exception(apiResponse.message ?: "Registration failed"))
            }
        } catch (e: HttpException) {
            Result.failure(e)
        } catch (e: IOException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Logs in an existing user with username and password.
     * On success, saves the returned access and refresh tokens locally.
     */
    suspend fun login(request: AuthRequest): Result<AuthResponse> {
        return try {
            val apiResponse = authApi.login(request)
            val authData = apiResponse.data
            if (apiResponse.success && authData != null) {
                authPreferences.saveTokens(
                    accessToken = authData.jwtToken ?: "",
                    refreshToken = authData.refreshToken ?: ""
                )
                Result.success(authData)
            } else {
                Result.failure(Exception(apiResponse.message ?: "Login failed"))
            }
        } catch (e: HttpException) {
            Result.failure(e)
        } catch (e: IOException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Requests new access and refresh tokens using an existing refresh token.
     */
    suspend fun refreshToken(request: RefreshTokenRequest): Result<RefreshTokenResponse> {
        return try {
            val apiResponse = authApi.refreshToken(request)
            val refreshData = apiResponse.data
            if (apiResponse.success && refreshData != null) {
                authPreferences.saveTokens(
                    accessToken = refreshData.jwtToken ?: "",
                    refreshToken = refreshData.refreshToken ?: ""
                )
                Result.success(refreshData)
            } else {
                Result.failure(Exception(apiResponse.message ?: "Token refresh failed"))
            }
        } catch (e: HttpException) {
            Result.failure(e)
        } catch (e: IOException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Logs out the current user and clears stored authentication tokens locally.
     */
    suspend fun logout(request: RefreshTokenRequest): Result<LogoutResponse> {
        return try {
            val apiResponse = authApi.logout(request)
            val logoutData = apiResponse.data ?: LogoutResponse(
                success = apiResponse.success,
                message = apiResponse.message ?: "",
                timestamp = apiResponse.timestamp ?: ""
            )
            if (apiResponse.success) {
                authPreferences.clearTokens()
            }
            Result.success(logoutData)
        } catch (e: HttpException) {
            Result.failure(e)
        } catch (e: IOException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
