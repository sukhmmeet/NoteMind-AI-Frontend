package com.dhaliwal.notemindai.network.api

import com.dhaliwal.notemindai.data.model.ApiResponse
import com.dhaliwal.notemindai.data.model.AuthRequest
import com.dhaliwal.notemindai.data.model.AuthResponse
import com.dhaliwal.notemindai.data.model.LogoutResponse
import com.dhaliwal.notemindai.data.model.RefreshTokenRequest
import com.dhaliwal.notemindai.data.model.RefreshTokenResponse
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Retrofit interface defining authentication network endpoints.
 */
interface AuthApiService {

    /**
     * Registers a new user account.
     * EndPoint: POST /api/v1/auth/signup
     */
    @POST("auth/signup")
    suspend fun register(
        @Body request: AuthRequest
    ): ApiResponse<AuthResponse>

    /**
     * Authenticates an existing user.
     * EndPoint: POST /api/v1/auth/login
     */
    @POST("auth/login")
    suspend fun login(
        @Body request: AuthRequest
    ): ApiResponse<AuthResponse>

    /**
     * Exchanges a valid refresh token for a new access token.
     * EndPoint: POST /api/v1/auth/refresh-token
     */
    @POST("auth/refresh-token")
    suspend fun refreshToken(
        @Body request: RefreshTokenRequest
    ): ApiResponse<RefreshTokenResponse>

    /**
     * Revokes the current refresh token and logs out the user.
     * EndPoint: POST /api/v1/auth/logout
     */
    @POST("auth/logout")
    suspend fun logout(
        @Body request: RefreshTokenRequest
    ): ApiResponse<LogoutResponse>
}
